package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.user.NewUserDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    public Map<Long, UserDto> getUsersMap(List<Long> userIds) {
        List<User> users = userRepository.findByIdIn(userIds);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<User> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findByIdIn(ids, pageable);
        } else {
            users = userRepository.findAll(pageable).getContent();
        }

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email " + request.getEmail() + " is already taken");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public void throwIfUserNotFound(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
