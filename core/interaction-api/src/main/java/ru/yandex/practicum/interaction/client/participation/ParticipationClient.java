package ru.yandex.practicum.interaction.client.participation;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCountDto;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;

import java.util.List;

@FeignClient(name = "request-service", path = "/api/request")
public interface ParticipationClient {

    /**
     * Counts participation requests for a specific event by status.
     *
     * @param eventId id of the event
     * @param status  status of the requests to count
     * @return count of requests with the specified status
     * @throws FeignException if a communication error occurs
     */
    @GetMapping("/count/{eventId}")
    int countByEventIdAndStatus(@PathVariable Long eventId, @RequestParam ParticipationStatus status) throws FeignException;

    /**
     * Counts confirmed participation requests for multiple events.
     *
     * @param eventIds list of event ids
     * @param status   status of the requests to count
     * @return list of DTOs containing event id and request count
     * @throws FeignException if a communication error occurs
     */
    @PostMapping("/count/confirmed")
    List<EventRequestCountDto> countConfirmedRequestsByEventIds(@RequestBody List<Long> eventIds,
                                                                @RequestParam ParticipationStatus status) throws FeignException;
}
