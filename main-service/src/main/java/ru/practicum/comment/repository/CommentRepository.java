package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment getCommentById(Long id);

    Page<Comment> findAllByAuthorId(Long author, Pageable pageable);

    Page<Comment> findAllByEventId(Long event, Pageable pageable);

    List<Comment> findAllByEventId(Long event);
}
