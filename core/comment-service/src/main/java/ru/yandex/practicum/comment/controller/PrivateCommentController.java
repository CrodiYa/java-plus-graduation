package ru.yandex.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.CommentShortDto;

import java.util.List;

public interface PrivateCommentController {

    /**
     * Creates a new comment for a specific event.
     *
     * @param userId  id of the user creating the comment
     * @param eventId id of the event being commented on
     * @param text    DTO containing the comment text
     * @return created comment DTO
     */
    @PostMapping("/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto addComment(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId,
                                 @RequestBody @Valid CommentShortDto text);

    /**
     * Updates an existing comment.
     *
     * @param userId    id of the user updating the comment
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to update
     * @param text      DTO containing the updated comment text
     * @return updated comment DTO
     */
    @PatchMapping("/{eventId}/comment/{commentId}")
    CommentDto patchComment(@PathVariable @Positive Long userId,
                                   @PathVariable @Positive Long eventId,
                                   @PathVariable @Positive Long commentId,
                                   @RequestBody @Valid CommentShortDto text);

    /**
     * Deletes a comment by the author.
     *
     * @param userId    id of the user deleting the comment
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to delete
     */
    @DeleteMapping("/{eventId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long eventId,
                              @PathVariable @Positive Long commentId) ;

    /**
     * Retrieves a paginated list of comments created by a specific user.
     *
     * @param userId id of the user whose comments to retrieve
     * @param sort   sorting order by creation date ("ASC" or "DESC"), default is "DESC"
     * @param from   the index of the first element to retrieve (0-based), default is 0
     * @param size   the number of elements to retrieve, default is 10
     * @return list of comment DTOs
     */
    @GetMapping("/comment")
    List<CommentDto> getUserComments(@PathVariable @Positive Long userId,
                                            @RequestParam(defaultValue = "DESC") String sort,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) ;
}
