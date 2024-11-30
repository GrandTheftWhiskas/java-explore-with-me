package ru.practicum.compilation.dto;

import ru.practicum.event.model.Event;

public interface EventByCompId {

    Long getCompilationId();

    Event getEvent();

}
