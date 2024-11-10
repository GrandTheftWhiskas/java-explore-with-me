package ru.practicum.stat_svc.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsDto;
import ru.practicum.EventDto;
import ru.practicum.stat_svc.service.StatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    @Autowired
    private final StatService statService;
    private static final String DATE = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public EventDto post(@Valid @RequestBody EventDto eventDto) {
        return statService.post(eventDto);
    }

    @GetMapping("/stats/{id}")
    public EventDto get(@PathVariable long id) {
        return statService.get(id);
    }

    @GetMapping("/stats")
    public List<StatsDto> getAll(@RequestParam @DateTimeFormat(pattern = DATE) String start,
                           @RequestParam @DateTimeFormat(pattern = DATE) String end,
                           @RequestParam(required = false) List<String> uris,
                           @RequestParam(required = false) boolean unique) {
        return statService.getAll(start, end, uris, unique);
    }
}
