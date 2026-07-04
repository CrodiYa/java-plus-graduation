package ru.yandex.practicum.participation.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface PrivateParticipationRequestController {

    /**
     * Retrieves all participation requests made by a specific user.
     *
     * @param userId id of the user who made the requests
     * @return list of participation request DTOs
     */
    @GetMapping
    List<ParticipationRequestDto> findByRequesterId(@PathVariable @Positive Long userId);

    /**
     * Creates a new participation request for a specific event.
     *
     * @param userId  id of the user creating the request
     * @param eventId id of the event to participate in
     * @return created participation request DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto addParticipationRequest(@PathVariable @Positive Long userId,
                                                    @RequestParam @Positive Long eventId);

    /**
     * Cancels an existing participation request.
     *
     * @param userId    id of the user canceling the request
     * @param requestId id of the request to cancel
     * @return canceled participation request DTO
     */
    @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto cancelParticipationRequest(@PathVariable @Positive Long userId,
                                                       @PathVariable @Positive Long requestId);
}
