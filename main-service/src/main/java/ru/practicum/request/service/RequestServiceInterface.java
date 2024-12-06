package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestServiceInterface {
    RequestDto add(Long userId, Long eventId);

    List<RequestDto> getAllOwn(Long userId);

    RequestDto cancel(Long userId, Long requestId);
}
