package ru.practicum.stat_svc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ValidationException;
import ru.practicum.stat_svc.dto.EventDto;
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

    public Event post(Event event) {
        log.info("Добавление события");
       if (event.getApp() == null || event.getApp().isBlank()) {
           throw new ValidationException("Название сервиса не может быть пустым");
       } else if (event.getUri() == null || event.getUri().isBlank()) {
           throw new ValidationException("Эндпоинт не может быть пустым");
       } else if (event.getIp() == null || event.getIp().isBlank()) {
           throw new ValidationException("Ip не может быть пустым");
       }

       event.setTime(LocalDateTime.now());
       return statRepository.save(event);
    }

    public Event get(Long id) {
        log.info("Получение статистики");
        return statRepository.getEventById(id);
    }

    public EventDto getAll(String app, String uri) {
        log.info("Получение списка статистики");
        List<Event> events = statRepository.getEvents(app, uri);
        EventDto eventDto = new EventDto();
        eventDto.setApp(app);
        eventDto.setUri(uri);
        eventDto.setHits(events.size());
        return eventDto;
    }

}
