package ru.yandex.practicum.ewm.service.user;

import ru.yandex.practicum.ewm.model.user.User;
import ru.yandex.practicum.ewm.model.user.NewUserRequest;
import ru.yandex.practicum.ewm.model.user.UserDto;

import java.util.List;

public interface UserService {

    User findEntityById(Long id);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest request);

    void deleteUser(Long userId);

    void throwIfUserNotFound(Long userId);
}