package ru.yandex.practicum.event.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.event.EventService;
import ru.yandex.practicum.interaction.dto.event.event.EventDtoRequest;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventState;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventControllerImpl implements AdminEventController {

    private final EventService eventService;

    @Override
    public List<EventFullDto> findAdminEvents(List<Long> users,
                                              List<EventState> states,
                                              List<Long> categories,
                                              String rangeStart,
                                              String rangeEnd,
                                              Integer from,
                                              Integer size) {

        return eventService.findAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Override
    public EventFullDto patchEvent(Long eventId,
                                   EventDtoRequest request) {

        return eventService.patchAdminEvent(eventId, request);
    }
}
