package ru.yandex.practicum.participation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.event.event.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.interaction.dto.event.event.UpdateEventRequestStatusDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface PrivateParticipationEventsController {

    /**
     * Retrieves all participation requests for a specific event.
     *
     * @param userId  id of the user requesting the list
     * @param eventId id of the event
     * @return list of participation request DTOs
     */
    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long eventId);

    /**
     * Updates the status of multiple participation requests for a specific event.
     *
     * @param userId  id of the user updating the requests
     * @param eventId id of the event
     * @param request DTO containing the list of request IDs and the new status
     * @return result containing lists of confirmed and rejected requests
     */
    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResultDto patchRequests(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long eventId,
                                                    @RequestBody @Valid UpdateEventRequestStatusDto request);

}
