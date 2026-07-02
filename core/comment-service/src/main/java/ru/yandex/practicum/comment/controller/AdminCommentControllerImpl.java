package ru.yandex.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.comment.service.CommentService;

@RestController
@RequestMapping("/admin/events/{eventId}")
@RequiredArgsConstructor
public class AdminCommentControllerImpl implements AdminCommentController {

    private final CommentService commentService;

    @Override
    public void deleteCommentAdmin(Long eventId,
                                   Long commentId) {

        commentService.deleteCommentAdmin(eventId, commentId);
    }
}
