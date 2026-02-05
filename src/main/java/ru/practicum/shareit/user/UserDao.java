package ru.practicum.shareit.user;

public interface UserDao {
    UserDto create(UserDto userDto);

    UserDto findOne(Long userId);

    UserDto delete(Long userId);

    UserDto update(Long userId, UserDto userDto);

}
