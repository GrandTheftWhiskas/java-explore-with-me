package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.dto.CompilationUpdate;
import ru.practicum.compilation.service.ServiceCompilation;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class CompilationControllerAdmin {
    private final ServiceCompilation service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponse add(@Valid @RequestBody CompilationDto dto) {
        log.info("Добавление подборки");
        return service.add(dto);
    }

    @PatchMapping("/{id}")
    public CompilationResponse update(@Valid @RequestBody CompilationUpdate dto, @PathVariable Long id) {
        log.info("Обновление подборки");
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Удаление подборки");
        service.delete(id);
    }
}
