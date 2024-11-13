package ru.practicum.stat_svc.mapper;

import ru.practicum.EventDto;
import ru.practicum.stat_svc.model.Event;

public class EventMapper {
    public static EventDto toEventDto(Event event) {
        return new EventDto(event.getApp(), event.getUri(), event.getIp(), event.getPeriod());
    }
}
