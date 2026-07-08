package ru.yandex.practicum.event.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventShortDto;

import java.time.Instant;
import java.util.List;

public interface PublicEventController {

    /**
     * Retrieves a paginated list of events based on specified filters.
     *
     * @param text          search text for filtering events by annotation or description (optional)
     * @param categories    list of category IDs to filter by (optional)
     * @param paid          filter by paid status (optional)
     * @param rangeStart    start date-time for filtering events after this date (optional)
     * @param rangeEnd      end date-time for filtering events before this date (optional)
     * @param onlyAvailable if true, returns only events with available slots
     * @param sort          sorting order ("EVENT_DATE" or "VIEWS"), default is no sorting
     * @param from          the index of the first element to retrieve (0-based), default is 0
     * @param size          the number of elements to retrieve, default is 10
     * @param request       HTTP servlet request for recording view statistics
     * @return list of short event DTOs
     */
    @GetMapping
    List<EventShortDto> findPublicEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) Instant rangeStart,
                                         @RequestParam(required = false) Instant rangeEnd,
                                         @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request);

    /**
     * Retrieves detailed information about a specific event.
     *
     * @param eventId id of the event to retrieve
     * @param request HTTP servlet request for recording view statistics
     * @return full event DTO
     */
    @GetMapping("/{id}")
    EventFullDto findPublicEvent(@PathVariable(name = "id") @Positive Long eventId,
                                 HttpServletRequest request);

    /**
     * Retrieves personalized event recommendations for a specific user.
     * Uses similarity calculations based on user actions to suggest relevant events.
     *
     * @param userId    id of the user requesting recommendations
     * @param maxResult maximum number of recommendations to return, default is 10
     * @return list of recommended short event DTOs
     */
    @GetMapping("/recommendations")
    List<EventShortDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") Long userId,
                                           @RequestParam(defaultValue = "10") Integer maxResult);

    /**
     * Adds a like from a user to a specific event.
     * Records the user's positive interaction with the event for similarity calculations.
     *
     * @param eventId id of the event to like
     * @param userId  id of the user adding the like
     */
    @PutMapping("/{eventId}/like")
    void addLike(@PathVariable @Positive Long eventId, @RequestHeader("X-EWM-USER-ID") Long userId);
}
