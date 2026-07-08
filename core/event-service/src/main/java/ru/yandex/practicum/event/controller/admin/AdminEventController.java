package ru.yandex.practicum.event.controller.admin;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.event.event.EventDtoRequest;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventState;
import ru.yandex.practicum.interaction.validation.OnUpdate;

import java.util.List;

import static ru.yandex.practicum.interaction.common.Formatter.PATTERN;


public interface AdminEventController {

    /**
     * Retrieves a paginated list of events based on specified filters.
     *
     * @param users      list of user ids to filter events by initiator (optional)
     * @param states     list of event states to filter by (optional)
     * @param categories list of category IDs to filter by (optional)
     * @param rangeStart start date-time for filtering events after this date (optional)
     * @param rangeEnd   end date-time for filtering events before this date (optional)
     * @param from       the index of the first element to retrieve (0-based), default is 0
     * @param size       the number of elements to retrieve, default is 10
     * @return list of full event DTOs
     */
    @GetMapping
    List<EventFullDto> findAdminEvents(@RequestParam(required = false) List<Long> users,
                                       @RequestParam(required = false) List<EventState> states,
                                       @RequestParam(required = false) List<Long> categories,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) String rangeStart,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) String rangeEnd,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size);

    /**
     * Updates an existing event by an administrator.
     * Admin can publish or reject events.
     *
     * @param eventId id of the event to update
     * @param request DTO containing the fields to update
     * @return updated full event DTO
     */
    @PatchMapping("/{eventId}")
    EventFullDto patchEvent(@PathVariable @Positive Long eventId,
                            @RequestBody @Validated(OnUpdate.class) EventDtoRequest request);
}
