package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.event.service.EventServiceAdmin;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
@Slf4j
public class EventControllerAdmin {
    private final EventServiceAdmin service;

    @PatchMapping("/{eventId}")
    public EventRespShort update(@Valid @RequestBody EventUpdate eventUpdate, @PathVariable Long eventId) {
        return service.update(eventUpdate, eventId);
    }

    @GetMapping
    public List<EventRespShort> get(@RequestParam(required = false) List<Long> users,
                                   @RequestParam(required = false) List<String> states,
                                   @RequestParam(required = false) List<Integer> categories,
                                   @RequestParam(required = false) @DateTimeFormat(
                                           pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                   @RequestParam(required = false) @DateTimeFormat(
                                           pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                   @RequestParam(required = false, defaultValue = "0") int from,
                                   @RequestParam(required = false, defaultValue = "10") int size) {
    log.info("Админ-поиск");
    List<EventRespShort> events = service.get(users, states, categories, rangeStart, rangeEnd, from, size);
    System.out.println(events);
    return events;
    }
}
