package ru.practicum.event.service;

import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestForConfirmation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventServiceInterface {
    EventRespShort update(EventUpdate eventUpdate, Long id);

    List<EventRespShort> get(List<Long> users, List<String> states, List<Integer> categories,
                             LocalDateTime start, LocalDateTime end, int from, int size);

    EventDto add(EventDto dto, Long userId);

    List<EventRespShort> get(Long userId, int from, int size);

    EventRespShort getFull(Long userId, Long eventId, String path);

    Map<String, List<RequestDto>> approve(RequestForConfirmation requestFor, Long userId, Long eventId);

    EventDto update(EventUpdate eventUpdate, Long userId, Long eventId);

    List<RequestDto> get(Long userId, Long eventId);

    List<EventRespShort> search(String text, List<Integer> categories, Boolean paid,
                                LocalDateTime start, LocalDateTime end,
                                Boolean available, String sort, int from, int size);

    EventRespShort get(Long eventId, String path);
}
