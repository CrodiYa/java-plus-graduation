package ru.yandex.practicum.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.user.NewUserDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;

import java.util.List;

public interface AdminUserController {

    /**
     * Retrieves a paginated list of users, optionally filtered by ids.
     *
     * @param ids  list of user ids to filter by (optional)
     * @param from the index of the first element to retrieve (0-based), default is 0
     * @param size the number of elements to retrieve, default is 10
     * @return list of user DTOs
     */
    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                           @RequestParam(defaultValue = "10") @Positive Integer size);

    /**
     * Creates a new user.
     *
     * @param request DTO containing user details
     * @return created user DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody @Valid NewUserDto request);

    /**
     * Deletes a user by its id.
     *
     * @param userId id of the user to delete
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable @Positive Long userId);

}
