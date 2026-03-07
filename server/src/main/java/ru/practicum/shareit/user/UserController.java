package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
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
    public UserDto update(@PathVariable(name = "id") long userId,
                          @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }
}
