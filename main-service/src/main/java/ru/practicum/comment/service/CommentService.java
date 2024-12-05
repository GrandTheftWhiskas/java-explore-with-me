package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    //PublicController
    public CommentDto add(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(userValidation(commentDto.getAuthor()));
        comment.setEvent(eventValidation(commentDto.getEvent()));
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto get(Long commentId) {
        return CommentMapper.toCommentDto(commentValidation(commentId));
    }

    public void delete(Long commentId) {
        Comment comment = commentValidation(commentId);
        commentRepository.delete(comment);
    }

    //AdminController
    public CommentDto update(CommentDto commentDto, Long commentId) {
        Comment comment = commentValidation(commentId);
        if (!commentDto.getAuthor().equals(comment.getAuthor().getId())) {
            throw new ValidationException("Данный пользователь рне является владельцем этого комментария");
        }

        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> getAll(Long authorId) {
        return commentRepository.findAllByAuthorId(authorId).stream()
                .map(comment -> CommentMapper.toCommentDto(comment)).toList();
    }

    //Validation
    public Comment commentValidation(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            return commentRepository.getCommentById(commentId);
        } else {
            throw new NotFoundException("Комментарий не найден");
        }
    }
    
    public User userValidation(Long userId) {
        if (userRepository.existsById(userId)) {
            return userRepository.getUserById(userId);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public Event eventValidation(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            return eventRepository.getEventById(eventId);
        } else {
            throw new NotFoundException("Событие не найдено");
        }
    }

}
