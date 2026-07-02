package ru.yandex.practicum.participation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.event.event.EventRequestStatusUpdateResultDto;
import ru.yandex.practicum.interaction.dto.event.event.UpdateEventRequestStatusDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;
import ru.yandex.practicum.participation.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateParticipationEventsControllerImpl implements PrivateParticipationEventsController {

    private final ParticipationRequestService participationService;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId,
                                                     Long eventId) {
        return participationService.findByEventId(userId, eventId);
    }

    @Override
    public EventRequestStatusUpdateResultDto patchRequests(Long userId,
                                                           Long eventId,
                                                           UpdateEventRequestStatusDto request) {
        return participationService.updateStatusParticipationRequest(userId, eventId, request);
    }
}
