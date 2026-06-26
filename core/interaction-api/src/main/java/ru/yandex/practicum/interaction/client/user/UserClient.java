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

    @GetMapping("/{userId}")
    UserDto getUserDtoById(@PathVariable Long userId) throws FeignException;

    @PostMapping("/map")
    Map<Long, UserDto> getUsersMap(@RequestBody List<Long> userIds) throws FeignException;

    @GetMapping("/{userId}/check")
    boolean existsById(@PathVariable Long userId) throws FeignException;
}
