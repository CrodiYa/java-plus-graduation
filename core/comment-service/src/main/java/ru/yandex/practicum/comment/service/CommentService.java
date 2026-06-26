package ru.yandex.practicum.comment.service;


import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.CommentDtoRequest;
import ru.yandex.practicum.interaction.exception.BadRequestException;
import ru.yandex.practicum.interaction.exception.ForbiddenException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.List;

public interface CommentService {

    /**
     * Creates a new comment for a specific event by a user.
     *
     * @param userId  id of the user creating the comment
     * @param eventId id of the event being commented on
     * @param request DTO containing comment details
     * @return created comment DTO
     * @throws NotFoundException   if the user or event does not exist
     * @throws BadRequestException if the comment content is invalid
     */
    CommentDto addComment(Long userId, Long eventId, CommentDtoRequest request);

    /**
     * Updates an existing comment.
     *
     * @param userId    id of the user updating the comment
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to update
     * @param request   DTO containing the fields to update
     * @return updated comment DTO
     * @throws NotFoundException   if the user, event, or comment does not exist
     * @throws BadRequestException if the updated content is invalid
     * @throws ForbiddenException  if the user is not the author of the comment
     */
    CommentDto patchComment(Long userId, Long eventId, Long commentId, CommentDtoRequest request);

    /**
     * Deletes a comment by a user.
     * Only the author of the comment can delete it.
     *
     * @param userId    id of the user deleting the comment
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to delete
     * @throws NotFoundException  if the user, event, or comment does not exist
     * @throws ForbiddenException if the user is not the author of the comment
     */
    void deleteComment(Long userId, Long eventId, Long commentId);

    /**
     * Deletes a comment by an administrator.
     * Admin can delete any comment regardless of authorship.
     *
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to delete
     * @throws NotFoundException if the event or comment does not exist
     */
    void deleteCommentAdmin(Long eventId, Long commentId);

    /**
     * Retrieves a paginated list of comments created by a specific user.
     *
     * @param userId id of the user whose comments to retrieve
     * @param sort   sorting order
     * @param from   the index of the first element to retrieve
     * @param size   the number of elements to retrieve
     * @return list of comment DTOs
     * @throws NotFoundException if the user does not exist
     */
    List<CommentDto> getUserComments(Long userId, String sort, Integer from, Integer size);

    /**
     * Retrieves a paginated list of comments for a specific event.
     *
     * @param eventId id of the event whose comments to retrieve
     * @param sort    sorting order
     * @param from    the index of the first element to retrieve
     * @param size    the number of elements to retrieve
     * @return list of comment DTOs
     * @throws NotFoundException if the event does not exist
     */
    List<CommentDto> getEventComments(Long eventId, String sort, Integer from, Integer size);

    /**
     * Retrieves a specific comment by its id.
     *
     * @param eventId   id of the event associated with the comment
     * @param commentId id of the comment to retrieve
     * @return comment DTO
     * @throws NotFoundException if the event or comment does not exist
     */
    CommentDto getCommentById(Long eventId, Long commentId);
}
