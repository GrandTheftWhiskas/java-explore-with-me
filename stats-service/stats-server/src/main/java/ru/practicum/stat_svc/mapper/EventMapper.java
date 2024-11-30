package ru.practicum.stat_svc.mapper;

import ru.practicum.EventDto;
import ru.practicum.stat_svc.model.Event;

import java.time.format.DateTimeFormatter;

public class EventMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static EventDto toEventDto(Event event) {
        return new EventDto(event.getApp(), event.getUri(), event.getIp(), event.getTimestamp().format(formatter));
    }
}
