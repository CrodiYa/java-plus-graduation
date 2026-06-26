package ru.yandex.practicum.participation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.client.participation.ParticipationClient;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCount;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;
import ru.yandex.practicum.participation.service.ParticipationServiceInternal;

import java.util.List;

@RestController
@RequestMapping(path = "/api/request")
@RequiredArgsConstructor
public class ParticipationControllerInternal implements ParticipationClient {

    private final ParticipationServiceInternal serviceInternal;

    @Override
    public int countByEventIdAndStatus(Long eventId, ParticipationStatus status) {
        return serviceInternal.countByEventIdAndStatus(eventId, status);
    }

    @Override
    public List<EventRequestCount> countConfirmedRequestsByEventIds(List<Long> eventIds, ParticipationStatus status) {
        return serviceInternal.countConfirmedRequestsByEventIds(eventIds, status);
    }
}
