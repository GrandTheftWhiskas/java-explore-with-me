package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto add(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Создание запроса");
        return requestService.add(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getAllOwn(@PathVariable Long userId) {
        log.info("Получение всех событий пользователя");
        return requestService.getAllOwn(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена запроса");
        return requestService.cancel(userId, requestId);
    }
}
