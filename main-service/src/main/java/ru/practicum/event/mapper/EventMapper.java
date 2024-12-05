package ru.practicum.event.mapper;

import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.model.Event;

public class EventMapper {
    public static EventRespShort toRespShort(Event event) {
        EventRespShort eventRespShort = new EventRespShort();
        eventRespShort.setId(event.getId());
        eventRespShort.setAnnotation(event.getAnnotation());
        eventRespShort.setCategory(event.getCategory());
        eventRespShort.setTitle(event.getTitle());
        eventRespShort.setEventDate(event.getEventDate());
        eventRespShort.setInitiator(event.getInit());
        eventRespShort.setPaid(event.getPaid());
        eventRespShort.setDescription(event.getDescription());
        eventRespShort.setParticipantLimit(event.getLimit());
        eventRespShort.setState(event.getState());
        eventRespShort.setCreatedOn(event.getCreated());
        eventRespShort.setLocation(event.getLocation());
        eventRespShort.setRequestModeration(event.getModeration());
        return eventRespShort;
    }

    public static Event toEvent(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
        event.setAnnotation(eventDto.getAnnotation());
        event.setDescription(eventDto.getDescription());
        event.setModeration(eventDto.getRequestModeration());
        event.setPaid(eventDto.isPaid());
        event.setLimit(eventDto.getParticipantLimit());
        event.setLocation(eventDto.getLocation());
        event.setEventDate(eventDto.getEventDate());
        return event;
    }

    public static EventDto toEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setAnnotation(event.getAnnotation());
        eventDto.setDescription(event.getDescription());
        eventDto.setRequestModeration(event.getModeration());
        eventDto.setPaid(event.getPaid());
        eventDto.setParticipantLimit(event.getLimit());
        eventDto.setLocation(event.getLocation());
        eventDto.setEventDate(event.getEventDate());
        eventDto.setInitiator(event.getInit().getId());
        eventDto.setCategory(event.getCategory().getId());
        eventDto.setState(event.getState());
        eventDto.setCreatedOn(event.getCreated());
        return eventDto;
    }
}
