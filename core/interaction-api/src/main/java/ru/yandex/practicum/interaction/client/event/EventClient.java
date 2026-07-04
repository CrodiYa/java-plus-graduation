package ru.yandex.practicum.interaction.client.event;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;

@FeignClient(name = "event-service", path = "/api/event")
public interface EventClient {

    /**
     * Retrieves full event DTO by its id.
     *
     * @param eventId id of the event to retrieve
     * @return full event DTO
     * @throws FeignException if the event does not exist or a communication error occurs
     */
    @GetMapping("/{eventId}")
    EventFullDto getEventFullDtoById(@PathVariable Long eventId) throws FeignException;

    /**
     * Checks if an event exists by its id.
     *
     * @param eventId id of the event to check
     * @return true if the event exists, false otherwise
     * @throws FeignException if a communication error occurs
     */
    @GetMapping("/{eventId}/check")
    boolean existsById(@PathVariable Long eventId) throws FeignException;
}
