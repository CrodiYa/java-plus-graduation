package ru.yandex.practicum.user.service;


import ru.yandex.practicum.interaction.dto.user.NewUserDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * Retrieves a userDto by its id.
     *
     * @param id id of the user to retrieve
     * @return user dto
     * @throws NotFoundException if the user with the given id does not exist
     */
    UserDto findUserDtoById(Long id);

    /**
     * Retrieves a map with userDto by its ids.
     *
     * @param userIds list of ids to retrieve
     * @return map of user dto
     */
    Map<Long, UserDto> getUsersMap(List<Long> userIds);

    /**
     * Retrieves a paginated list of users, optionally filtered by ids.
     *
     * @param ids  list of user ids to filter by (optional, can be null or empty)
     * @param from the index of the first element to retrieve (0-based)
     * @param size the number of elements to retrieve
     * @return list of user DTOs
     */
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    /**
     * Creates a new user.
     *
     * @param request DTO containing user details
     * @return created user DTO
     * @throws ConflictException if a user with the same email or name already exists
     */
    UserDto createUser(NewUserDto request);

    /**
     * Deletes a user by its id.
     *
     * @param userId id of the user to delete
     * @throws NotFoundException if the user with the given id does not exist
     */
    void deleteUser(Long userId);

    /**
     * Checks if a user with the specified id exists.
     * Throws an exception if the user is not found.
     *
     * @param userId id of the user to check
     * @throws NotFoundException if the user with the given id does not exist
     */
    void throwIfUserNotFound(Long userId);

    boolean existsById(Long userId);
}