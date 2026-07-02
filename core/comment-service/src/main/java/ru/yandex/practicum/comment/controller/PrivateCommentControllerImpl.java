package ru.yandex.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.CommentShortDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateCommentControllerImpl implements PrivateCommentController {

    private final CommentService commentService;

    @Override
    public CommentDto addComment(Long userId,
                                 Long eventId,
                                 CommentShortDto text) {
        return commentService.addComment(userId, eventId, text);
    }

    @Override
    public CommentDto patchComment(Long userId,
                                   Long eventId,
                                   Long commentId,
                                   CommentShortDto text) {
        return commentService.patchComment(userId, eventId, commentId, text);

    }

    @Override
    public void deleteComment(Long userId,
                              Long eventId,
                              Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId,
                                            String sort,
                                            Integer from,
                                            Integer size) {

        return commentService.getUserComments(userId, sort, from, size);
    }
}
