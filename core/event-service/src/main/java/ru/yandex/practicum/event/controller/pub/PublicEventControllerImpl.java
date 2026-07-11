package ru.yandex.practicum.event.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.event.EventService;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventShortDto;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventControllerImpl implements PublicEventController {
    private final EventService eventService;

    @Override
    public List<EventShortDto> findPublicEvents(String text, List<Long> categories, Boolean paid,
                                                Instant rangeStart, Instant rangeEnd, boolean onlyAvailable,
                                                String sort, Integer from, Integer size,
                                                HttpServletRequest request) {
        return eventService.findPublicEvents(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
    }

    @Override
    public EventFullDto findPublicEvent(Long eventId,
                                        HttpServletRequest request) {
        return eventService.findPublicEvent(eventId, request.getRemoteAddr());
    }

    @Override
    public List<EventShortDto> getRecommendations(Long userId, Integer maxResult) {
        return eventService.getRecommendations(userId, maxResult);
    }

    @Override
    public void addLike(Long eventId, Long userId) {
        eventService.addLike(eventId, userId);
    }
}
