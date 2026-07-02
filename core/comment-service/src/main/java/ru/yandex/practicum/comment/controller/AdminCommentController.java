package ru.yandex.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface AdminCommentController {

    /**
     * Deletes a comment by an administrator.
     * Admin can delete any comment regardless of authorship.
     *
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to delete
     */
    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCommentAdmin(@PathVariable @Positive Long eventId,
                                   @PathVariable @Positive Long commentId);
}
