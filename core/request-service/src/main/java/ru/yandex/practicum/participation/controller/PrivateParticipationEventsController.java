package ru.yandex.practicum.participation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.event.event.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.interaction.dto.event.event.EventRequestStatusUpdateResult;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;
import ru.yandex.practicum.participation.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateParticipationEventsController {

    private final ParticipationRequestService participationService;

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId,
                                                     @PathVariable @Positive Long eventId) {
        return participationService.findByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequests(@PathVariable @Positive Long userId,
                                                        @PathVariable @Positive Long eventId,
                                                        @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return participationService.updateStatusParticipationRequest(userId, eventId, request);
    }
}
