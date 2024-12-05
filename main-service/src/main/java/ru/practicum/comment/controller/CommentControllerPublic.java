package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class CommentControllerPublic {
    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto add(@Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария");
        return service.add(commentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto get(@PathVariable Long commentId) {
        log.info("Получение комментария по ID");
        return service.get(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        log.info("Удаление комментария");
        service.delete(commentId);
    }
}
