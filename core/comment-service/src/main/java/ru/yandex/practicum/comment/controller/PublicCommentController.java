package ru.yandex.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;

import java.util.List;

public interface PublicCommentController {

    /**
     * Retrieves a paginated list of comments for a specific event.
     *
     * @param eventId id of the event to retrieve comments for
     * @param sort    sorting order by creation date ("ASC" or "DESC"), default is "DESC"
     * @param from    the index of the first element to retrieve (0-based), default is 0
     * @param size    the number of elements to retrieve, default is 10
     * @return list of comment DTOs
     */
    @GetMapping("/{eventId}/comment")
    List<CommentDto> getEventComments(@PathVariable @Positive Long eventId,
                                      @RequestParam(defaultValue = "DESC") String sort,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size);

    /**
     * Retrieves a specific comment by its id for a given event.
     *
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to retrieve
     * @return comment DTO
     */
    @GetMapping("/{eventId}/comment/{commentId}")
    CommentDto getCommentById(@PathVariable @Positive Long eventId,
                              @PathVariable @Positive Long commentId);
}
