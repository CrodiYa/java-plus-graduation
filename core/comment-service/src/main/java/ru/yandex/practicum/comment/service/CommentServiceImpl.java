package ru.yandex.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comment.mapper.CommentMapper;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.interaction.client.event.EventClient;
import ru.yandex.practicum.interaction.client.user.UserClient;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.CommentShortDto;
import ru.yandex.practicum.interaction.dto.event.event.EventFullDto;
import ru.yandex.practicum.interaction.dto.event.event.EventState;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.interaction.exception.BadRequestException;
import ru.yandex.practicum.interaction.exception.ForbiddenException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final EventClient eventClient;
    private final UserClient userClient;

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public CommentDto addComment(Long userId, Long eventId, CommentShortDto request) {
        EventFullDto event = eventClient.getEventFullDtoById(eventId);

        throwIfEventNotPublished(event);

        UserDto user = userClient.getUserDtoById(userId);

        Comment comment = commentMapper.toComment(request);
        comment.setAuthorId(userId);
        comment.setAuthorName(user.getName());
        comment.setEventId(eventId);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto patchComment(Long userId, Long eventId, Long commentId, CommentShortDto request) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        if (!eventClient.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }

        Comment comment = findEntityById(commentId);

        validateCommentBelongsToEvent(comment, eventId);

        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Only author can change comments");
        }

        comment.setText(request.getText());
        comment.setUpdated(Instant.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        if (!eventClient.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        Comment comment = findEntityById(commentId);

        validateCommentBelongsToEvent(comment, eventId);

        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Only author can change comments");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentAdmin(Long eventId, Long commentId) {
        if (!eventClient.existsById(eventId)) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        }
        Comment comment = findEntityById(commentId);
        validateCommentBelongsToEvent(comment, eventId);

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, String sort, Integer from, Integer size) {
        if (!userClient.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        Sort sorting = getSorting(sort);

        return commentRepository.findByAuthorId(userId, PageRequest.of(from / size, size, sorting)).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, String sort, Integer from, Integer size) {
        EventFullDto event = eventClient.getEventFullDtoById(eventId);
        throwIfEventNotPublished(event);

        Sort sorting = getSorting(sort);

        return commentRepository.findByEventId(eventId, PageRequest.of(from / size, size, sorting)).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    public CommentDto getCommentById(Long eventId, Long commentId) {
        EventFullDto event = eventClient.getEventFullDtoById(eventId);
        throwIfEventNotPublished(event);

        Comment comment = findEntityById(commentId);
        validateCommentBelongsToEvent(comment, eventId);

        return commentMapper.toDto(comment);
    }

    /**
     * Retrieves a comment entity by its id.
     *
     * @param commentId id of the comment to retrieve
     * @return comment entity
     * @throws NotFoundException if the comment with the given id does not exist
     */
    private Comment findEntityById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " not found"));
    }

    /**
     * Creates a Sort object for ordering comments by creation date.
     * Default sorting is descending (newest first). If the provided sort parameter
     * equals "ASC" (case-insensitive), returns ascending order.
     *
     * @param sort sorting order string ("ASC" for ascending, any other value for descending)
     * @return Sort object configured for the specified order
     */
    private Sort getSorting(String sort) {
        Sort sorting = Sort.by("created").descending();
        if (sort.equalsIgnoreCase("ASC")) {
            sorting = sorting.ascending();
        }

        return sorting;
    }

    /**
     * Validates that a comment belongs to the specified event.
     *
     * @param comment comment entity to validate
     * @param eventId id of the event the comment should belong to
     * @throws NotFoundException if the comment does not belong to the specified event
     */
    private void validateCommentBelongsToEvent(Comment comment, Long eventId) {
        if (!comment.getEventId().equals(eventId)) {
            throw new NotFoundException("Comment with id: " + comment.getId() + " not found for event with id: " + eventId);
        }
    }

    /**
     * Checks if an event is in the PUBLISHED state.
     * Throws an exception if the event is not published.
     *
     * @param event full event DTO to validate
     * @throws BadRequestException if the event is not published
     */
    private void throwIfEventNotPublished(EventFullDto event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event is not published");
        }
    }
}
