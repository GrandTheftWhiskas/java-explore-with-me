package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.model.Compilation;

public class CompilationMapper {
    public static CompilationResponse toCompilationResponse(Compilation compilation) {
        CompilationResponse compilationResponse = new CompilationResponse();
        compilationResponse.setId(compilation.getId());
        compilationResponse.setTitle(compilation.getTitle());
        compilationResponse.setPinned(compilation.getPinned());
        return compilationResponse;
    }
}
