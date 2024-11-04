package ru.practicum.stat_svc.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.stat_svc.dto.EventDto;
import ru.practicum.stat_svc.model.Event;
import ru.practicum.stat_svc.service.StatService;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    @Autowired
    private final StatService statService;

    @PostMapping("/hit")
    public Event post(@RequestBody Event event) {
        return statService.post(event);
    }

    @GetMapping("/stats/{id}")
    public Event get(@PathVariable long id) {
        return statService.get(id);
    }

    @GetMapping("/stats")
    public EventDto getAll(@RequestParam String app, @RequestParam String uri) {
        return statService.getAll(app, uri);
    }
}
