package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Create new user {}", userDto);
        return userDao.create(userDto);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Update userId={} to {}", userId, userDto);
        return userDao.update(userId, userDto);
    }

    @Override
    public UserDto findOne(Long userId) {
        log.info("Find user by userId={}", userId);
        return userDao.findOne(userId);
    }

    @Override
    public UserDto delete(Long userId) {
        log.info("Delete user by userId={}", userId);
        return userDao.delete(userId);
    }
}
