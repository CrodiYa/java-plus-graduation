package ru.yandex.practicum.interaction.client.user;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.user.UserDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserClient {

    /**
     * Retrieves user DTO by its id.
     *
     * @param userId id of the user to retrieve
     * @return user DTO
     * @throws FeignException if the user does not exist or a communication error occurs
     */
    @GetMapping("/{userId}")
    UserDto getUserDtoById(@PathVariable Long userId) throws FeignException;

    /**
     * Retrieves a map of user DTOs for the provided user ids.
     *
     * @param userIds list of user ids
     * @return map where key is user id and value is user DTO
     * @throws FeignException if a communication error occurs
     */
    @PostMapping("/map")
    Map<Long, UserDto> getUsersMap(@RequestBody List<Long> userIds) throws FeignException;

    /**
     * Checks if a user exists by its id.
     *
     * @param userId id of the user to check
     * @return true if the user exists, false otherwise
     * @throws FeignException if a communication error occurs
     */
    @GetMapping("/{userId}/check")
    boolean existsById(@PathVariable Long userId) throws FeignException;
}
