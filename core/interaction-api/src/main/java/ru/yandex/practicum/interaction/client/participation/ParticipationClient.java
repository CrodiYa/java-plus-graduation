package ru.yandex.practicum.interaction.client.participation;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCount;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;

import java.util.List;

@FeignClient(name = "participation-service", path = "/api/participation")
public interface ParticipationClient {

    @GetMapping("/count/{eventId}")
    int countByEventIdAndStatus(@PathVariable Long eventId, @RequestParam ParticipationStatus status) throws FeignException;

    @PostMapping("/count/confirmed")
    List<EventRequestCount> countConfirmedRequestsByEventIds(@RequestBody List<Long> eventIds,
                                                             @RequestParam ParticipationStatus status) throws FeignException;
}
