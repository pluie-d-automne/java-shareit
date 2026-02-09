package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Create new user {}", userDto);
        return userRepository.create(userDto);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Update userId={} to {}", userId, userDto);
        return userRepository.update(userId, userDto);
    }

    @Override
    public UserDto findOne(Long userId) {
        log.info("Find user by userId={}", userId);
        return userRepository.findOne(userId);
    }

    @Override
    public UserDto delete(Long userId) {
        log.info("Delete user by userId={}", userId);
        return userRepository.delete(userId);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }
}
