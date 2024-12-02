package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;

public class CompilationMapper {
    public static CompilationResponse toCompilationResponse(Compilation compilation) {
        CompilationResponse compilationResponse = new CompilationResponse();
        compilationResponse.setId(compilation.getId());
        compilationResponse.setTitle(compilation.getTitle());
        compilationResponse.setPinned(compilation.getPinned());
        compilationResponse.setEvents(compilation.getEvents().stream()
                .map(event -> EventMapper.toRespShort(event)).toList());
        return compilationResponse;
    }
}
