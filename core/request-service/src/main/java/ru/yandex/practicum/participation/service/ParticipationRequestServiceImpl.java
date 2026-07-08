package ru.yandex.practicum.participation.service;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.CollectorClient;
import ru.yandex.practicum.grpc.stats.action.ActionTypeProto;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;
import ru.yandex.practicum.interaction.client.event.EventClient;
import ru.yandex.practicum.interaction.client.user.UserClient;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.interaction.dto.event.event.EventState;
import ru.yandex.practicum.interaction.dto.event.event.UpdateEventRequestStatusDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.participation.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.participation.model.ParticipationRequest;
import ru.yandex.practicum.participation.repository.ParticipationRequestRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository repository;
    private final ParticipationRequestMapper mapper;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final CollectorClient collectorClient;

    @Override
    public List<ParticipationRequestDto> findByRequesterId(Long requesterId) {
        return repository.findByRequesterId(requesterId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        EventFullDto event = eventClient.getEventFullDtoById(eventId);

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
                .requesterId(userId)
                .eventId(eventId)
                .status(ParticipationStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || limit == 0) {
            request.setStatus(ParticipationStatus.CONFIRMED);
        }

        UserActionProto userAction = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.ACTION_REGISTER)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        collectorClient.sendUserAction(userAction);

        return mapper.toDto(repository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        ParticipationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));

        if (!request.getRequesterId().equals(userId)) {
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
    public EventRequestStatusUpdateResultDto updateStatusParticipationRequest(Long userId, Long eventId,
                                                                              UpdateEventRequestStatusDto request) {
        EventFullDto event = getEventAndVerifyOwner(userId, eventId);

        int limit = event.getParticipantLimit();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        boolean isModerationOff = !event.getRequestModeration() || limit == 0;
        boolean idsEmpty = request.getRequestIds() == null || request.getRequestIds().isEmpty();

        if (isModerationOff || idsEmpty) {
            return EventRequestStatusUpdateResultDto.builder()
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

        return EventRequestStatusUpdateResultDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    /**
     * Retrieves a full event DTO by its id and verifies that the specified user is the initiator.
     * Throws an exception if the user does not own the event.
     *
     * @param userId  id of the user who should own the event
     * @param eventId id of the event to retrieve
     * @return full event DTO
     * @throws NotFoundException if the event does not exist or the user is not the owner
     */
    private EventFullDto getEventAndVerifyOwner(Long userId, Long eventId) {
        EventFullDto event = eventClient.getEventFullDtoById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        return event;
    }
}
