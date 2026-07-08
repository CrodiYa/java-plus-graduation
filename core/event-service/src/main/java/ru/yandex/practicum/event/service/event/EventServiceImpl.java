package ru.yandex.practicum.event.service.event;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.CollectorClient;
import ru.yandex.practicum.client.RecommendationClient;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.mapper.EventStateMapper;
import ru.yandex.practicum.event.model.category.Category;
import ru.yandex.practicum.event.model.event.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.specification.AdminEventSpecification;
import ru.yandex.practicum.event.repository.specification.PublicEventSpecification;
import ru.yandex.practicum.event.service.category.CategoryService;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.action.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.interaction.client.participation.ParticipationClient;
import ru.yandex.practicum.interaction.client.user.UserClient;
import ru.yandex.practicum.interaction.dto.event.event.*;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCountDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.interaction.exception.BadRequestException;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.yandex.practicum.interaction.common.Formatter.toInstant;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final long HOURS_BEFORE_START_USER = 2;
    private static final long HOURS_BEFORE_START_ADMIN = 1;
    private static final String SORT_BY_VIEWS = "VIEWS";

    private final UserClient userClient;
    private final ParticipationClient participationClient;
    private final CollectorClient collectorClient;
    private final RecommendationClient recommendationClient;
    private final CategoryService categoryService;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventStateMapper eventStateMapper;

    @Override
    public Event findEntityById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id " + id + " not found"));
    }

    @Override
    public EventFullDto findEventFullDtoById(Long id) {
        Event event = findEntityById(id);
        return assemblyFullDto(event);
    }

    @Override
    public List<EventFullDto> findAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                              String rangeStart, String rangeEnd, Integer from, Integer size) {

        Instant start = getRangeInstant(rangeStart);
        Instant end = getRangeInstant(rangeEnd);

        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new BadRequestException("Start can`t be after end");
            }
        }

        AdminEventSpecification spec = new AdminEventSpecification(users, states, categories, start, end);

        List<Event> events = eventRepository.findAll(spec, PageRequest.of(from / size, size)).getContent();

        List<Long> ids = events.stream().map(Event::getId).toList();
        Map<Long, Double> ratingMap = getRatingMap(ids);
        Map<Long, Integer> confirmedMap = getConfirmedMap(ids);

        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        Map<Long, UserDto> usersMap = userClient.getUsersMap(userIds);

        return events.stream()
                .map(event -> {
                    EventFullDto dto = eventMapper.toFullDto(event);
                    dto.setRating(ratingMap.getOrDefault(event.getId(), 0.0));
                    dto.setConfirmedRequests(confirmedMap.getOrDefault(event.getId(), 0));
                    dto.setInitiator(usersMap.get(event.getInitiatorId()));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<EventShortDto> findPublicEvents(String text, List<Long> categories, Boolean paid,
                                                Instant rangeStart, Instant rangeEnd, boolean onlyAvailable,
                                                String sort, Integer from, Integer size, String ip) {

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException("Start can`t be after end");
            }
        }

        PublicEventSpecification spec = new PublicEventSpecification(rangeStart, rangeEnd, paid, categories, text);

        List<Event> events = eventRepository
                .findAll(spec, PageRequest.of(from / size, size, Sort.by("eventDate")))
                .getContent();

        List<Long> ids = events.stream().map(Event::getId).toList();

        Map<Long, Double> ratingMap = getRatingMap(ids);
        Map<Long, Integer> confirmedMap = getConfirmedMap(ids);

        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        Map<Long, UserDto> usersMap = userClient.getUsersMap(userIds);

        Stream<EventShortDto> stream = events.stream()
                .flatMap(event -> {
                    Integer confirmedRequests = confirmedMap.getOrDefault(event.getId(), 0);
                    if (onlyAvailable && confirmedRequests > event.getParticipantLimit()) {
                        return Stream.empty();
                    }
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setRating(ratingMap.getOrDefault(event.getId(), 0.0));
                    dto.setConfirmedRequests(confirmedRequests);
                    dto.setInitiator(usersMap.get(event.getInitiatorId()));
                    return Stream.of(dto);
                });

        if (SORT_BY_VIEWS.equals(sort)) {
            return stream.sorted(Comparator.comparing(EventShortDto::getRating).reversed())
                    .toList();
        }

        return stream.toList();
    }

    @Override
    public EventFullDto findPublicEvent(Long eventId, String ip) {
        Event event = findEntityById(eventId);

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Even can`t be found");
        }

        EventFullDto dto = eventMapper.toFullDto(event);
        UserDto userDto = userClient.getUserDtoById(event.getInitiatorId());

        int confirmedRequests = participationClient.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);
        Map<Long, Double> ratingMap = getRatingMap(Collections.singletonList(eventId));
        dto.setInitiator(userDto);
        dto.setRating(ratingMap.getOrDefault(eventId, 0.0));
        dto.setConfirmedRequests(confirmedRequests);
        return dto;
    }

    @Override
    public List<EventShortDto> findEventsByUserId(Long userId, Integer from, Integer size) {
        UserDto userDto = userClient.getUserDtoById(userId);

        return eventRepository.findByInitiatorId(userId, PageRequest.of(from / size, size)).stream()
                .map(eventMapper::toShortDto)
                .peek(eventShortDto -> eventShortDto.setInitiator(userDto))
                .toList();
    }

    @Override
    public EventFullDto findEventById(Long userId, Long eventId) {
        UserDto userDto = userClient.getUserDtoById(userId);

        Event event = findEntityById(eventId);

        if (!event.getInitiatorId().equals(userId)) {
            throw new BadRequestException("UserId must match initiatorId");
        }

        return assemblyFullDto(event, userDto);
    }

    @Override
    public EventFullDto addEvent(Long userId, EventDtoRequest request) {
        try {
            EventValidator.throwIfDateInvalid(request.getEventDate(), HOURS_BEFORE_START_USER);

            UserDto user = userClient.getUserDtoById(userId);
            Category category = categoryService.findEntityById(request.getCategory());

            Event event = eventMapper.toEvent(request);

            if (event.getParticipantLimit() == null) event.setParticipantLimit(0);
            if (event.getPaid() == null) event.setPaid(false);
            if (event.getRequestModeration() == null) event.setRequestModeration(true);

            event.setInitiatorId(user.getId());
            event.setCategory(category);
            event.setState(EventState.PENDING);

            Event saved = eventRepository.save(event);

            return assemblyFullDto(saved, user);

        } catch (DataIntegrityViolationException e) {
            log.debug("Conflict during saving event [{}]", request, e);
            throw new ConflictException("Conflict with another event");
        }
    }

    @Override
    public EventFullDto patchEvent(Long userId, Long eventId, EventDtoRequest request) {
        UserDto userDto = userClient.getUserDtoById(userId);
        Event event = patchEvent(eventId, request, HOURS_BEFORE_START_USER, false);

        return assemblyFullDto(event, userDto);
    }

    @Override
    public EventFullDto patchAdminEvent(Long eventId, EventDtoRequest request) {
        Event event = patchEvent(eventId, request, HOURS_BEFORE_START_ADMIN, true);
        return assemblyFullDto(event);
    }

    @Override
    public List<EventShortDto> getRecommendations(Long userId, Integer maxResult) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return recommendationClient.getRecommendationsForUser(userId, maxResult)
                .map(recommendedEventProto -> {
                    Event event = findEntityById(recommendedEventProto.getEventId());
                    EventShortDto dto = eventMapper.toShortDto(event);
                    dto.setRating(recommendedEventProto.getScore());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long eventId, Long userId) {
        Event event = findEntityById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Cannot like unpublished event");
        }

        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        UserActionProto userAction = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_LIKE)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        collectorClient.sendUserAction(userAction);
    }

    @Override
    public void throwIfEventNotFound(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
    }

    @Override
    public boolean existsById(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    private Event patchEvent(Long eventId, EventDtoRequest request, long hoursBeforeStart, boolean isAdmin) {
        try {
            if (request.getEventDate() != null) {
                EventValidator.throwIfDateInvalid(request.getEventDate(), hoursBeforeStart);
            }

            Event event = findEntityById(eventId);
            EventStateAction action = request.getStateAction();
            EventValidator.throwIfStateTransitionInvalid(action, event.getState(), isAdmin);

            if (action != null) {
                EventState newState = isAdmin
                        ? eventStateMapper.mapAdminEventAction(action)
                        : eventStateMapper.mapUserEventAction(action);

                if (EventState.PUBLISHED.equals(newState)) {
                    event.setPublishedOn(Instant.now());
                }
                if (newState != null) {
                    event.setState(newState);
                }
            }

            if (request.getCategory() != null) {
                Category category = categoryService.findEntityById(request.getCategory());
                event.setCategory(category);
            }

            eventMapper.merge(event, request);

            return eventRepository.save(event);

        } catch (DataIntegrityViolationException e) {
            log.debug("Conflict during patching event [{}]", request, e);
            throw new ConflictException("Conflict with another event");
        }
    }

    /**
     * Assembles a full event DTO from an event entity.
     * Retrieves the initiator information via user client.
     *
     * @param event event entity to convert
     * @return full event DTO with initiator information
     */
    private EventFullDto assemblyFullDto(Event event) {
        UserDto initiatorDto = userClient.getUserDtoById(event.getInitiatorId());
        EventFullDto fullDto = eventMapper.toFullDto(event);
        fullDto.setInitiator(initiatorDto);
        return fullDto;
    }

    /**
     * Assembles a full event DTO from an event entity and a pre-fetched initiator DTO.
     * Used to avoid additional client calls when initiator is already available.
     *
     * @param event     event entity to convert
     * @param initiator pre-fetched initiator DTO
     * @return full event DTO with initiator information
     */
    private EventFullDto assemblyFullDto(Event event, UserDto initiator) {
        EventFullDto fullDto = eventMapper.toFullDto(event);
        fullDto.setInitiator(initiator);
        return fullDto;
    }

    /**
     * Converts a date string to an Instant.
     * Returns null if the provided string is null.
     *
     * @param date date string to convert
     * @return Instant representation of the date, or null if input is null
     */
    private Instant getRangeInstant(String date) {
        if (date != null) {
            return toInstant(date);
        }

        return null;
    }

    /**
     * Retrieves view rating for the specified event ids from the stats service.
     * Returns an empty map if no event ids are provided or if no stats are available.
     *
     * @param eventIds list of event ids to retrieve views for
     * @return map where key is the event ID and value is the hit count
     */
    private Map<Long, Double> getRatingMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Collections.emptyMap();

        return recommendationClient.getInteractionsCount(eventIds)
                .collect(Collectors.toMap(
                        RecommendedEventProto::getEventId,
                        RecommendedEventProto::getScore));
    }

    /**
     * Retrieves the count of confirmed participation requests for the specified event ids.
     * Returns an empty map if no event ids are provided.
     *
     * @param eventIds list of event ids to retrieve confirmed request counts for
     * @return map where key is the event id and value is the count of confirmed requests
     */
    private Map<Long, Integer> getConfirmedMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Collections.emptyMap();

        return participationClient.countConfirmedRequestsByEventIds(eventIds, ParticipationStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(
                        EventRequestCountDto::getEventId,
                        EventRequestCountDto::getCount
                ));
    }
}
