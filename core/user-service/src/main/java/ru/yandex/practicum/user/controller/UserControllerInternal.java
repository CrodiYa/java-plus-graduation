package ru.yandex.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.client.user.UserClient;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
public class UserControllerInternal implements UserClient {

    private final UserService userService;

    @Override
    public UserDto getUserDtoById(Long userId) {
        return userService.findUserDtoById(userId);
    }

    @Override
    public Map<Long, UserDto> getUsersMap(List<Long> userIds) {
        return userService.getUsersMap(userIds);
    }

    @Override
    public boolean existsById(Long userId) {
        return userService.existsById(userId);
    }
}
