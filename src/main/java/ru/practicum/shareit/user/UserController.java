package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Marker;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findOne(@PathVariable(name = "id") long userId) {
        return userService.findOne(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") long userId) {
        userService.delete(userId);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public UserDto update(@PathVariable(name = "id") long userId,
                       @Valid @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }
}
