package ru.yandex.practicum.event.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.event.EventService;
import ru.yandex.practicum.interaction.client.event.EventClient;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;

@RestController
@RequestMapping(path = "/api/event")
@RequiredArgsConstructor
public class EventControllerInternal implements EventClient {

    private final EventService eventService;

    @Override
    public EventFullDto getEventFullDtoById(Long eventId) {
        return eventService.findEventFullDtoById(eventId);
    }

    @Override
    public boolean existsById(Long eventId) {
        return eventService.existsById(eventId);
    }
}
