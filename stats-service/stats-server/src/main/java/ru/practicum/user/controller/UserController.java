package ru.practicum.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.stat_svc.controller.StatsController;
import ru.practicum.user.Create;
import ru.practicum.user.Update;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;


@Controller
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;
    private final StatsController statsController;

    @PostMapping
    @Validated(Create.class)
    public ResponseEntity<Object> post(@RequestBody @Validated(Create.class) UserDto user,
                                       HttpServletRequest request) {
        log.info("Добавление пользователя");
        ResponseEntity<Object> result = userClient.post(user);
        statsController.post(new EventDto("userService", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now()));
        return result;
    }

    @PatchMapping("/{userId}")
    @Validated(Update.class)
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody @Validated(Update.class) UserDto user,
                                         HttpServletRequest request) {
        log.info("Обновление пользователя");
        ResponseEntity<Object> result = userClient.update(userId, user);
        statsController.post(new EventDto("userService", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now()));
        return result;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId, HttpServletRequest request) {
        log.info("Получение пользователя");
        ResponseEntity<Object> result = userClient.get(userId);
        statsController.post(new EventDto("userService", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now()));
        return result;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId, HttpServletRequest request) {
        log.info("Удаление пользователя");
        ResponseEntity<Object> result = userClient.delete(userId);
        statsController.post(new EventDto("userService", request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now()));
        return result;
    }
}
