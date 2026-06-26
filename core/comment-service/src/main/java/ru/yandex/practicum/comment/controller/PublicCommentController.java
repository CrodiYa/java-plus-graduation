package ru.yandex.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/{eventId}/comment")
    public List<CommentDto> getEventComments(@PathVariable @Positive Long eventId,
                                             @RequestParam(defaultValue = "DESC") String sort,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {

        return commentService.getEventComments(eventId, sort, from, size);
    }

    @GetMapping("/{eventId}/comment/{commentId}")
    public CommentDto getCommentById(@PathVariable @Positive Long eventId,
                                     @PathVariable @Positive Long commentId) {

        return commentService.getCommentById(eventId, commentId);
    }
}
