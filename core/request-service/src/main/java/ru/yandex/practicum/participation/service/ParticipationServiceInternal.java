package ru.yandex.practicum.participation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCount;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;
import ru.yandex.practicum.participation.repository.ParticipationRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationServiceInternal {

    private final ParticipationRequestRepository requestRepository;

    public int countByEventIdAndStatus(Long eventId, ParticipationStatus status) {
        return requestRepository.countByEventIdAndStatus(eventId, status);
    }

    public List<EventRequestCount> countConfirmedRequestsByEventIds(List<Long> eventIds, ParticipationStatus status) {
        return requestRepository.countConfirmedRequestsByEventIds(eventIds, status);
    }
}
