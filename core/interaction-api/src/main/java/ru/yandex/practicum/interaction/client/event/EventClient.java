package ru.yandex.practicum.interaction.client.event;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;

@FeignClient(name = "event-service", path = "/api/event")
public interface EventClient {

    @GetMapping("/{eventId}")
    EventFullDto getEventFullDtoById(@PathVariable Long eventId) throws FeignException;

    @GetMapping("/{eventId}/check")
    boolean existsById(@PathVariable Long eventId) throws FeignException;
}
