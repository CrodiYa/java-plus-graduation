package ru.yandex.practicum.ewm.service.participation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.exception.ConflictException;
import ru.yandex.practicum.ewm.exception.NotFoundException;
import ru.yandex.practicum.ewm.mappers.ParticipationRequestMapper;
import ru.yandex.practicum.ewm.model.event.Event;
import ru.yandex.practicum.ewm.model.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.ewm.model.event.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.model.event.EventState;
import ru.yandex.practicum.ewm.model.participation.ParticipationRequest;
import ru.yandex.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.yandex.practicum.ewm.model.participation.ParticipationStatus;
import ru.yandex.practicum.ewm.model.user.User;
import ru.yandex.practicum.ewm.repository.ParticipationRequestRepository;
import ru.yandex.practicum.ewm.service.event.EventService;
import ru.yandex.practicum.ewm.service.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository repository;
    private final ParticipationRequestMapper mapper;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<ParticipationRequestDto> findByRequesterId(Long requesterId) {
        return repository.findByRequesterId(requesterId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        User requester = userService.findEntityById(userId);
        Event event = eventService.findEntityById(eventId);

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Can`t participate in not published event");
        }

        if (repository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator can`t put request on it`s own event");
        }

        Integer limit = event.getParticipantLimit();

        if (limit != 0 && repository.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED) >= limit) {
            throw new ConflictException("Max limit reached");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .requester(requester)
                .event(event)
                .status(ParticipationStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || limit == 0) {
            request.setStatus(ParticipationStatus.CONFIRMED);
        }

        return mapper.toDto(repository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Can`t cancel foreign request");
        }
        request.setStatus(ParticipationStatus.CANCELED);
        return mapper.toDto(repository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findByEventId(Long userId, Long eventId) {
        getEventAndVerifyOwner(userId, eventId);
        return repository.findByEventId(eventId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusParticipationRequest(Long userId, Long eventId,
                                                                           EventRequestStatusUpdateRequest request) {
        Event event = getEventAndVerifyOwner(userId, eventId);

        int limit = event.getParticipantLimit();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        log.info("requestModeration={}, participantLimit={}", event.getRequestModeration(), limit);

        boolean isModerationOff = !event.getRequestModeration() || limit == 0;
        boolean idsEmpty = request.getRequestIds() == null || request.getRequestIds().isEmpty();

        if (isModerationOff || idsEmpty) {
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(Collections.emptyList())
                    .rejectedRequests(Collections.emptyList())
                    .build();
        }

        int countConfirmed = repository.countByEventIdAndStatus(eventId, ParticipationStatus.CONFIRMED);
        List<ParticipationRequest> requests = repository.findAllByIdIn(request.getRequestIds());

        if (ParticipationStatus.CONFIRMED.equals(request.getStatus()) && countConfirmed >= limit) {
            throw new ConflictException("Max limit reached");
        }

        for (ParticipationRequest pr : requests) {
            if (!ParticipationStatus.PENDING.equals(pr.getStatus())) {
                throw new ConflictException("Status can be changed only in pending requests");
            }

            if (ParticipationStatus.CONFIRMED.equals(request.getStatus()) && countConfirmed < limit) {
                pr.setStatus(ParticipationStatus.CONFIRMED);
                countConfirmed++;
                confirmedRequests.add(mapper.toDto(pr));
            } else {
                pr.setStatus(ParticipationStatus.REJECTED);
                rejectedRequests.add(mapper.toDto(pr));
            }
        }

        repository.saveAll(requests);

        if (ParticipationStatus.CONFIRMED.equals(request.getStatus()) && countConfirmed >= limit) {
            repository.rejectPendingRequests(eventId, ParticipationStatus.PENDING);
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private Event getEventAndVerifyOwner(Long userId, Long eventId) {
        Event event = eventService.findEntityById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        return event;
    }
}
