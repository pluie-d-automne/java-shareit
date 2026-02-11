package ru.practicum.shareit.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private List<User> users = new ArrayList<>();
    private Long idCounter = 0L;
    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        duplicateEmailCheck(userDto.getEmail(), -1L);
        User user = new User(generateId(), userDto.getName(), userDto.getEmail());
        users.add(user);
        log.info("Created user {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userFound = findUserById(userId);
        String newName = userDto.getName();
        String newEmail = userDto.getEmail();

        if (newName != null) {
            userFound.setName(newName);
        }

        if (newEmail != null && !newEmail.isBlank()) {
            duplicateEmailCheck(userDto.getEmail(), userId);
            userFound.setEmail(userDto.getEmail());
        }
        log.info("Updated user {}", userFound);
        return userMapper.toUserDto(userFound);
    }

    @Override
    public UserDto findOne(Long userId) {
        User user = findUserById(userId);
        log.info("Found user {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto delete(Long userId) {
        User userFound = findUserById(userId);
        users.remove(userFound);
        log.info("Deleted user {}", userFound);
        return userMapper.toUserDto(userFound);
    }

    private Long generateId() {
        idCounter += 1L;
        log.info("Generated new userId={}", idCounter);
        return idCounter;
    }

    private Boolean duplicateEmailCheck(String userEmail, Long userId) {
        Optional<User> userFound = users.stream().filter(user -> user.getEmail().equals(userEmail)).findFirst();

        if (userFound.isPresent() && !userFound.get().getId().equals(userId)) {
            throw new ConflictException("Пользователь с email " + userEmail + " уже существует.");
        }

        return false;
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> userFound = users.stream().filter(user -> Objects.equals(user.getId(), userId)).findFirst();

        if (userFound.isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        } else {
            return userFound.get();
        }
    }
}


