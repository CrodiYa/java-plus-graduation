package ru.yandex.practicum.participation.service;


import ru.yandex.practicum.interaction.dto.event.event.UpdateEventRequestStatusDto;
import ru.yandex.practicum.interaction.dto.event.event.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.List;

public interface ParticipationRequestService {

    /**
     * Retrieves all participation requests made by a specific user.
     *
     * @param requesterId id of the user who made the requests
     * @return list of participation request DTOs
     */
    List<ParticipationRequestDto> findByRequesterId(Long requesterId);

    /**
     * Creates a new participation request for a specific event by a user.
     *
     * @param userId  id of the user creating the request
     * @param eventId id of the event to participate in
     * @return created participation request DTO
     * @throws NotFoundException if the user or event does not exist
     * @throws ConflictException if the user is the initiator of the event,
     *                           if the event is not published,
     *                           if the participant limit has been reached,
     *                           or if a duplicate request already exists
     */
    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    /**
     * Cancels an existing participation request.
     *
     * @param userId    id of the user canceling the request
     * @param requestId id of the request to cancel
     * @return canceled participation request DTO with status updated to CANCELED
     * @throws NotFoundException if the user or request does not exist
     * @throws ConflictException if the request is not owned by the user
     */
    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);

    /**
     * Retrieves all participation requests for a specific event.
     * This method is used by event initiators to view incoming requests.
     *
     * @param userId  id of the user requesting the list
     * @param eventId id of the event
     * @return list of participation request DTOs
     * @throws NotFoundException if the user or event does not exist
     * @throws ConflictException if the user is not the initiator of the event
     */
    List<ParticipationRequestDto> findByEventId(Long userId, Long eventId);

    /**
     * Updates the status of multiple participation requests for a specific event.
     * Allows the event initiator to confirm or reject pending requests.
     *
     * @param userId  id of the user updating the requests
     * @param eventId id of the event
     * @param request DTO containing the list of request IDs and the new status
     * @return result containing lists of confirmed and rejected requests
     * @throws NotFoundException if the user, event, or any of the requests do not exist
     * @throws ConflictException if the user is not the initiator of the event,
     *                           if the event is not published,
     *                           if the participant limit has been reached,
     *                           or if trying to confirm already confirmed/rejected requests
     */
    EventRequestStatusUpdateResultDto updateStatusParticipationRequest(
            Long userId, Long eventId, UpdateEventRequestStatusDto request);
}
