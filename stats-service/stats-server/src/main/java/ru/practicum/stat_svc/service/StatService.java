package ru.practicum.stat_svc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EventDto;
import ru.practicum.StatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.stat_svc.mapper.EventMapper;
import ru.practicum.stat_svc.model.Event;
import ru.practicum.stat_svc.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatService {
    @Autowired
    private final StatRepository statRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventDto post(EventDto eventDto) {
        log.info("Добавление события");
        Event event = new Event();
        event.setApp(eventDto.getApp());
        event.setUri(eventDto.getUri());
        event.setIp(eventDto.getIp());
        event.setTime(LocalDateTime.now());
        return EventMapper.toEventDto(statRepository.save(event));
    }

    public EventDto get(Long id) {
        log.info("Получение события");
        return EventMapper.toEventDto(statRepository.getEventById(id));
    }

    public StatsDto getAll(LocalDateTime start, LocalDateTime end, String uri, Boolean unique) {
        log.info("Получение статистики");
        if (start.isAfter(end)) {
            throw new ValidationException("Конец не может быть перед стартом");
        }

        List<Event> events = statRepository.getEvents(null, null, null, null);
        StatsDto eventDto = new StatsDto();
        eventDto.setApp(null);
        eventDto.setUri(uri);
        eventDto.setHits(events.size());
        return null;
    }
}
