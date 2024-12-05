package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.service.EventService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPublic {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService service;
    private final StatClient statClient;
    private static final String app = "ewm-main-service";

    @GetMapping
    public List<EventRespShort> search(@RequestParam(required = false) String text,
                                       @RequestParam(required = false) List<Integer> categories,
                                       @RequestParam(required = false) Boolean paid,
                                       @RequestParam(required = false) String rangeStart,
                                       @RequestParam(required = false) String rangeEnd,
                                       @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
                                       @RequestParam(required = false) String sort,
                                       @Min(0) @RequestParam(defaultValue = "0") int from,
                                       @Min(1) @RequestParam(defaultValue = "10") int size,
                                       HttpServletRequest servletRequest) {
        log.info("Поиск события");
        String ip = servletRequest.getRemoteAddr();
        String path = servletRequest.getRequestURI();
        EventDto eventDto = new EventDto(app, path, ip, LocalDateTime.now().format(formatter));
        statClient.addStat(eventDto);
        if (rangeStart == null && rangeEnd == null) {
            return service.search(text, categories, paid, null, null, onlyAvailable, sort, from, size);
        }

        String newStart = URLDecoder.decode(rangeStart, StandardCharsets.UTF_8);
        String newEnd = URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8);
        LocalDateTime start = LocalDateTime.parse(newStart, formatter);
        LocalDateTime end = LocalDateTime.parse(newEnd, formatter);
        return service.search(text, categories, paid, start, end, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventRespShort getEvent(@PathVariable Long id, HttpServletRequest servletRequest) {
        log.info("Получение события");
        String path = servletRequest.getRequestURI();
        String ip = servletRequest.getRemoteAddr();
        EventDto eventDto = new EventDto("ewm-main-service", path, ip, LocalDateTime.now().format(formatter));
        statClient.addStat(eventDto);
        return service.get(id, path);
    }
}
