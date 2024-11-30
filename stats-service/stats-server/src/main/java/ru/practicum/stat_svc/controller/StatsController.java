package ru.practicum.stat_svc.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto post(@Valid @RequestBody EventDto eventDto) {
        log.info("Добавление события");
        return statService.post(eventDto);
    }

    @GetMapping("/stats/{id}")
    public EventDto get(@PathVariable long id) {
        log.info("Получение события");
        return statService.get(id);
    }

    @GetMapping("/stats")
    public List<StatsDto> getAll(@RequestParam(required = false) @DateTimeFormat(pattern = DATE) String start,
                           @RequestParam(required = false) @DateTimeFormat(pattern = DATE) String end,
                           @RequestParam(required = false) List<String> uris,
                           @RequestParam(required = false) boolean unique) {
        log.info("Получение статистики");
        return statService.getAll(start, end, uris, unique);
    }
}
