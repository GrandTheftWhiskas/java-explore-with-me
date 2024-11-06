package ru.practicum.stat_svc.mapper;

import ru.practicum.dto.EventDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.stat_svc.model.Event;

public class EventMapper {
    public static EventDto toEventDto(Event event) {
        try {
            return new EventDto(event.getApp(),
                    event.getUri(), event.getIp(), event.getTime());
        } catch (NullPointerException e) {
            throw new NotFoundException("Событие не найдено");
        }
    }
}
