package ru.yandex.practicum.event.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.Formatter;
import ru.yandex.practicum.dto.StatsRequest;
import ru.yandex.practicum.dto.ViewStatsDto;
import ru.yandex.practicum.event.mapper.EventMapper;
import ru.yandex.practicum.event.mapper.EventStateMapper;
import ru.yandex.practicum.event.model.category.Category;
import ru.yandex.practicum.event.model.event.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.repository.specification.AdminEventSpecification;
import ru.yandex.practicum.event.repository.specification.PublicEventSpecification;
import ru.yandex.practicum.event.service.category.CategoryService;
import ru.yandex.practicum.interaction.client.participation.ParticipationClient;
import ru.yandex.practicum.interaction.client.user.UserClient;
import ru.yandex.practicum.interaction.dto.event.event.*;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCount;
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

import static ru.yandex.practicum.dto.Formatter.toInstant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final long HOURS_BEFORE_START_USER = 2;
    private static final long HOURS_BEFORE_START_ADMIN = 1;
    private static final String APP_NAME = "ewm-main-service";
    private static final String SORT_BY_VIEWS = "VIEWS";
    private static final String EVENT_KEY = "/events/";

    private final UserClient userClient;
    private final CategoryService categoryService;
    private final EventRepository eventRepository;
    private final ParticipationClient participationClient;
    private final EventMapper eventMapper;
    private final EventStateMapper eventStateMapper;
    private final StatsClient statsClient;

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
        Map<String, Long> viewsMap = getViewsMap(ids);
        Map<Long, Integer> confirmedMap = getConfirmedMap(ids);

        List<Long> userIds = events.stream().map(Event::getInitiatorId).toList();
        Map<Long, UserDto> usersMap = userClient.getUsersMap(userIds);

        return events.stream()
                .map(event -> {
                    EventFullDto dto = eventMapper.toFullDto(event);
                    dto.setViews(viewsMap.getOrDefault(EVENT_KEY + event.getId(), 0L));
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
        statsClient.hit(EndpointHitDto.builder()
                .app(APP_NAME)
                .uri("/events")
                .ip(ip)
                .timestamp(Formatter.format(Instant.now()))
                .build());

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

        Map<String, Long> viewsMap = getViewsMap(ids);
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
                    dto.setViews(viewsMap.getOrDefault(EVENT_KEY + event.getId(), 0L));
                    dto.setConfirmedRequests(confirmedRequests);
                    dto.setInitiator(usersMap.get(event.getInitiatorId()));
                    return Stream.of(dto);
                });

        if (SORT_BY_VIEWS.equals(sort)) {
            return stream.sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .toList();
        }

        return stream.toList();
    }

    @Override
    public EventFullDto findPublicEvent(Long eventId, String ip) {
        statsClient.hit(EndpointHitDto.builder()
                .app(APP_NAME)
                .uri(EVENT_KEY + eventId)
                .ip(ip)
                .timestamp(Formatter.format(Instant.now()))
                .build());

        Event event = findEntityById(eventId);

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Even can`t be found");
        }

        EventFullDto dto = eventMapper.toFullDto(event);
        UserDto userDto = userClient.getUserDtoById(event.getInitiatorId());

        int confirmedRequests = participationClient.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);
        Map<String, Long> viewsMap = getViewsMap(Collections.singletonList(eventId));
        dto.setInitiator(userDto);
        dto.setViews(viewsMap.getOrDefault(EVENT_KEY + eventId, 0L));
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

    private EventFullDto assemblyFullDto(Event event) {
        UserDto initiatorDto = userClient.getUserDtoById(event.getInitiatorId());
        EventFullDto fullDto = eventMapper.toFullDto(event);
        fullDto.setInitiator(initiatorDto);
        return fullDto;
    }

    private EventFullDto assemblyFullDto(Event event, UserDto initiator) {
        EventFullDto fullDto = eventMapper.toFullDto(event);
        fullDto.setInitiator(initiator);
        return fullDto;
    }

    private Instant getRangeInstant(String date) {
        if (date != null) {
            return toInstant(date);
        }

        return null;
    }

    private Map<String, Long> getViewsMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Collections.emptyMap();

        List<String> uris = eventIds.stream().map(id -> EVENT_KEY + id).toList();

        StatsRequest statsRequest = StatsRequest.builder()
                .start(Formatter.format(Instant.EPOCH))
                .end(Formatter.format(Instant.now().plusSeconds(1)))
                .uris(uris)
                .unique(true)
                .build();

        List<ViewStatsDto> stats = statsClient.getStats(statsRequest);
        if (stats == null || stats.isEmpty()) return Collections.emptyMap();

        return stats.stream()
                .collect(Collectors.toMap(
                        ViewStatsDto::getUri,
                        ViewStatsDto::getHits,
                        Long::sum
                ));
    }

    private Map<Long, Integer> getConfirmedMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Collections.emptyMap();

        return participationClient.countConfirmedRequestsByEventIds(eventIds, ParticipationStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(
                        EventRequestCount::getEventId,
                        EventRequestCount::getCount
                ));
    }
}
