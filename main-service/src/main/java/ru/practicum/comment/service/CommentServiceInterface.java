package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

interface CommentServiceInterface {
    CommentDto add(CommentDto commentDto);

    CommentDto get(Long commentId);

    CommentDto update(CommentDto commentDto, Long commentId);

    List<CommentDto> getAll(Long authorId, int from, int size);

    List<CommentDto> getAllByEvent(Long eventId, int from, int size);

    void delete(Long commentId);
}
