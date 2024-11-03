package ru.practicum.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat_svc.controller.StatsController;
import ru.practicum.stat_svc.model.Event;
import ru.practicum.user.Create;
import ru.practicum.user.Update;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.UserDto;


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
    public ResponseEntity<Object> post(@RequestBody @Validated(Create.class) UserDto user) {
        log.info("Добавление пользователя");
        ResponseEntity<Object> result = userClient.post(user);
        return result;
    }

    @PatchMapping("/{userId}")
    @Validated(Update.class)
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody @Validated(Update.class) UserDto user) {
        log.info("Обновление пользователя");
        return userClient.update(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        log.info("Получение пользователя");
        return userClient.get(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Удаление пользователя");
        return userClient.delete(userId);
    }
}
