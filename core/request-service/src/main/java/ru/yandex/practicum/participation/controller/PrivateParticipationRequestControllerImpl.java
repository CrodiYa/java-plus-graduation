package ru.yandex.practicum.participation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;
import ru.yandex.practicum.participation.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateParticipationRequestControllerImpl implements PrivateParticipationRequestController {

    private final ParticipationRequestService service;

    @Override
    public List<ParticipationRequestDto> findByRequesterId(Long userId) {
        return service.findByRequesterId(userId);
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId,
                                                           Long eventId) {
        return service.addParticipationRequest(userId, eventId);
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId,
                                                              Long requestId) {
        return service.cancelParticipationRequest(userId, requestId);
    }
}
