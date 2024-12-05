package ru.practicum.request.mapper;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(), request.getRequester().getId(), request.getEvent().getId(),
                request.getStatus(), request.getCreated());
    }
}
