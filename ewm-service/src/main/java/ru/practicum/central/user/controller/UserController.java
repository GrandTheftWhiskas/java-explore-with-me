package ru.practicum.central.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.central.user.Create;
import ru.practicum.central.user.Update;
import ru.practicum.central.user.dto.UserDto;
import ru.practicum.central.user.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    @Autowired
    private final UserService userService;

    @PostMapping
    @Validated(Create.class)
    public UserDto post(@RequestBody UserDto user) {
        return userService.post(user);
    }

    @PatchMapping("/{userId}")
    @Validated(Update.class)
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto user) {
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
