package ru.yandex.practicum.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAuthorId(Long authorId, PageRequest pageRequest);

    List<Comment> findByEventId(Long eventId, PageRequest pageRequest);
}
