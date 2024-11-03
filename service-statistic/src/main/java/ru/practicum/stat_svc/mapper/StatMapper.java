package ru.practicum.stat_svc.mapper;

import ru.practicum.exception.NotFoundException;
import ru.practicum.stat_svc.dto.EventDto;
import ru.practicum.stat_svc.model.Event;

public class StatMapper {
    public static EventDto toEventDto(Event event) {
        try {
            EventDto eventDto = new EventDto();
            eventDto.setApp(event.getApp());
            eventDto.setUri(eventDto.getUri());
            return eventDto;
        } catch (NullPointerException e) {
            throw new NotFoundException("Событие не найдено");
        }
    }
}
