package ru.yandex.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.CommentDtoRequest;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId,
                                 @RequestBody @Valid CommentDtoRequest text) {
        return commentService.addComment(userId, eventId, text);
    }

    @PatchMapping("/{eventId}/comment/{commentId}")
    public CommentDto patchComment(@PathVariable @Positive Long userId,
                                   @PathVariable @Positive Long eventId,
                                   @PathVariable @Positive Long commentId,
                                   @RequestBody @Valid CommentDtoRequest text) {
        return commentService.patchComment(userId, eventId, commentId, text);

    }

    @DeleteMapping("/{eventId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long eventId,
                              @PathVariable @Positive Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }

    @GetMapping("/comment")
    public List<CommentDto> getUserComments(@PathVariable @Positive Long userId,
                                            @RequestParam(defaultValue = "DESC") String sort,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) {

        return commentService.getUserComments(userId, sort, from, size);
    }
}
