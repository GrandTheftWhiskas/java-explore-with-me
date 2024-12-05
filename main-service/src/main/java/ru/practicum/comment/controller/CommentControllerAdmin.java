package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Slf4j
public class CommentControllerAdmin {
    private final CommentService service;

    @PatchMapping("/{commentId}")
    public CommentDto update(@Valid @RequestBody CommentDto commentDto, @PathVariable Long commentId) {
        log.info("Редактирование комментария");
        return service.update(commentDto, commentId);
    }

    @GetMapping
    public List<CommentDto> getAll(@RequestParam(required = false) Long authorId) {
        log.info("Получение всех комментариев пользователя");
        return service.getAll(authorId);
    }
}
