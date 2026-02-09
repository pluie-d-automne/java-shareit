package ru.practicum.shareit.user;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto findOne(Long userId);

    UserDto delete(Long userId);

    UserDto update(Long userId, UserDto userDto);

    User findUserById(Long userId);
}
