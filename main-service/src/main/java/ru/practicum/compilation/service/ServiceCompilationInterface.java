package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.dto.CompilationUpdate;

import java.util.List;

public interface ServiceCompilationInterface {
    CompilationResponse add(CompilationDto dto);

    CompilationResponse update(CompilationUpdate dto, Long id);

    CompilationResponse get(Long id);

    List<CompilationResponse> getAll(boolean pinned, int from, int size);

    void delete(Long id);
}
