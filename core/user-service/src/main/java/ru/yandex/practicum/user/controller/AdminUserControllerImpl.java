package ru.yandex.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.user.NewUserDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserControllerImpl implements AdminUserController {

    private final UserService userService;

    @Override
    public List<UserDto> getUsers(List<Long> ids,
                                  Integer from,
                                  Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @Override
    public UserDto createUser(NewUserDto request) {
        return userService.createUser(request);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
}
