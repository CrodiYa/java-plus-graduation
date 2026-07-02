package ru.yandex.practicum.event.controller.priv;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.event.event.EventDtoRequest;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventShortDto;
import ru.yandex.practicum.interaction.validation.OnCreate;
import ru.yandex.practicum.interaction.validation.OnUpdate;

import java.util.List;

public interface PrivateEventController {

    /**
     * Retrieves a paginated list of events created by a specific user.
     *
     * @param userId id of the user whose events to retrieve
     * @param from   the index of the first element to retrieve (0-based), default is 0
     * @param size   the number of elements to retrieve, default is 10
     * @return list of short event DTOs
     */
    @GetMapping
    List<EventShortDto> findEventsByUserId(@PathVariable @Positive Long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size);

    /**
     * Retrieves detailed information about a specific event for a user.
     *
     * @param userId  id of the user requesting the event
     * @param eventId id of the event to retrieve
     * @return full event DTO
     */
    @GetMapping("/{eventId}")
    EventFullDto findEventById(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long eventId);

    /**
     * Creates a new event for the specified user.
     *
     * @param userId  id of the user creating the event
     * @param request DTO containing event details
     * @return created full event DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addEvent(@PathVariable @Positive Long userId,
                          @RequestBody @Validated(OnCreate.class) EventDtoRequest request);

    /**
     * Updates an existing event for the specified user.
     *
     * @param userId  id of the user updating the event
     * @param eventId id of the event to update
     * @param request DTO containing the fields to update
     * @return updated full event DTO
     */
    @PatchMapping("/{eventId}")
    EventFullDto patchEvent(@PathVariable @Positive Long userId,
                            @PathVariable @Positive Long eventId,
                            @RequestBody @Validated(OnUpdate.class) EventDtoRequest request);
}
