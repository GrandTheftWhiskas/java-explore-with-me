package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.service.ServiceCompilationPublic;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
@Slf4j
public class CompilationControllerPublic {
    private final ServiceCompilationPublic service;

    @GetMapping
    public List<CompilationResponse> get(@RequestParam(required = false) boolean pinned,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("Получение подборок");
        return service.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationResponse get(@PathVariable Long compId) {
        log.info("Получение подборки");
        return service.get(compId);
    }
}
