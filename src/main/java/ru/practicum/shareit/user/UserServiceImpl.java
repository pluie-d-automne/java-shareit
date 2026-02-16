package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        user = userRepository.save(user);
        log.info("Create new user {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = findUserById(userId);
        String newName = userDto.getName();
        String newEmail = userDto.getEmail();

        if (newName != null) {
            user.setName(newName);
        }

        if (newEmail != null && !newEmail.isBlank()) {
            duplicateEmailCheck(userDto.getEmail(), userId);
            user.setEmail(userDto.getEmail());
        }

        log.info("Update userId={} to {}", userId, user);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto findOne(Long userId) {
        log.info("Find user by userId={}", userId);
        User user = findUserById(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        User user = findUserById(userId);
        log.info("Delete user {}", user);
        userRepository.delete(user);
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);

            if (user.isEmpty()) {
                throw new NotFoundException("User with id=" + userId + " was not found.");
            } else {
                User userFound = user.get();
                log.info("Found user {} by user_id {}", userFound, userId);
                return userFound;
            }
    }

    private Boolean duplicateEmailCheck(String userEmail, Long userId) {
        Optional<User> userFound = userRepository.findByEmailIgnoreCase(userEmail);

        if (userFound.isPresent() && !userFound.get().getId().equals(userId)) {
            throw new ConflictException("Пользователь с email " + userEmail + " уже существует.");
        }

        return false;
    }
}
