package ru.yandex.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicCommentControllerImpl implements PublicCommentController {

    private final CommentService commentService;

    @Override
    public List<CommentDto> getEventComments(Long eventId,
                                             String sort,
                                             Integer from,
                                             Integer size) {

        return commentService.getEventComments(eventId, sort, from, size);
    }

    @Override
    public CommentDto getCommentById(Long eventId,
                                     Long commentId) {

        return commentService.getCommentById(eventId, commentId);
    }
}
