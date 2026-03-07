package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public  ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Object> findUser(@PathVariable(name = "id") long userId) {
        log.info("Get user by id {}", userId);
        return userClient.findUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "id") long userId) {
        log.info("Delete user by id {}", userId);
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public  ResponseEntity<Object> updateUser(@PathVariable(name = "id") long userId,
                                          @Valid @RequestBody UserDto userDto) {
        log.info("Update user {}, id={}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }
}
