package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService implements CommentServiceInterface {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    //PublicController
    @Transactional
    public CommentDto add(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(giveUser(commentDto.getAuthor()));
        comment.setEvent(giveEvent(commentDto.getEvent()));
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto get(Long commentId) {
        return CommentMapper.toCommentDto(giveComment(commentId));
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = giveComment(commentId);
        commentRepository.delete(comment);
    }

    //AdminController
    @Transactional
    public CommentDto update(CommentDto commentDto, Long commentId) {
        Comment comment = giveComment(commentId);
        if (!commentDto.getAuthor().equals(comment.getAuthor().getId())) {
            throw new ValidationException("Данный пользователь рне является владельцем этого комментария");
        }

        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> getAll(Long authorId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByAuthorId(authorId, pageable).stream()
                .map(comment -> CommentMapper.toCommentDto(comment)).toList();
    }

    public List<CommentDto> getAllByEvent(Long eventId, int from, int size) {
        giveEvent(eventId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(comment -> CommentMapper.toCommentDto(comment)).toList();
    }

    //Validation
    public Comment giveComment(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            return commentRepository.getCommentById(commentId);
        } else {
            throw new NotFoundException("Комментарий не найден");
        }
    }


    public User giveUser(Long userId) {
        if (userRepository.existsById(userId)) {
            return userRepository.getUserById(userId);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public Event giveEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            return eventRepository.getEventById(eventId);
        } else {
            throw new NotFoundException("Событие не найдено");
        }
    }

}
