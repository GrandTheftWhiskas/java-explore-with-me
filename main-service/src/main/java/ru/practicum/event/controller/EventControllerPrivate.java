package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestForConfirmation;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPrivate {
    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody EventDto eventDto, @PathVariable Long userId) {
        log.info("Добавление события");
        return service.add(eventDto, userId);
    }

    @GetMapping
    public List<EventRespShort> getUserEvents(@PathVariable Long userId,
                                              @Min(0) @RequestParam(defaultValue = "0") int from,
                                              @Min(1) @RequestParam(defaultValue = "10") int size) {
        log.info("Получение событий пользователя");
        return service.get(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventRespShort getFullEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                      HttpServletRequest servletRequest) {
        log.info("Получение события");
        return service.getFull(userId, eventId, servletRequest.getRequestURI());
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable Long userId, @PathVariable Long eventId,
                           @Valid @RequestBody EventUpdate eventUpdate) {
        log.info("Обновление события");
        return service.update(eventUpdate, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Получение запросов по событию");
        return service.get(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public Map<String, List<RequestDto>> approve(@RequestBody RequestForConfirmation request,
                                                 @PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Обновление запроса по событию");
        return service.approve(request, userId, eventId);
    }
}
