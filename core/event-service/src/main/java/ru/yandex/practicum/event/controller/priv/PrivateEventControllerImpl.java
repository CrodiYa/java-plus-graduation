package ru.yandex.practicum.event.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.event.EventService;
import ru.yandex.practicum.interaction.dto.event.event.EventDtoRequest;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventShortDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventControllerImpl implements PrivateEventController {

    private final EventService eventService;

    @Override
    public List<EventShortDto> findEventsByUserId(Long userId,
                                                  Integer from,
                                                  Integer size) {
        return eventService.findEventsByUserId(userId, from, size);
    }

    @Override
    public EventFullDto findEventById(Long userId,
                                      Long eventId) {
        return eventService.findEventById(userId, eventId);
    }

    @Override
    public EventFullDto addEvent(Long userId,
                                 EventDtoRequest request) {
        return eventService.addEvent(userId, request);
    }

    @Override
    public EventFullDto patchEvent(Long userId,
                                   Long eventId,
                                   EventDtoRequest request) {
        return eventService.patchEvent(userId, eventId, request);
    }
}
