package ru.practicum.stat_svc.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsDto;
import ru.practicum.EventDto;
import ru.practicum.stat_svc.service.StatService;

import java.time.LocalDateTime;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    @Autowired
    private final StatService statService;

    @PostMapping("/hit")
    public EventDto post(@RequestBody EventDto event) {
        return statService.post(event);
    }

    @GetMapping("/stats/{id}")
    public EventDto get(@PathVariable long id) {
        return statService.get(id);
    }

    @GetMapping("/stats")
    public StatsDto getAll(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end,
                           @RequestParam String uri, Boolean unique) {
        return statService.getAll(start, end, uri, unique);
    }
}
