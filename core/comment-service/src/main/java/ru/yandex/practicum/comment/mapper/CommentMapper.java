package ru.yandex.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.dto.Formatter;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.CommentShortDto;

@Mapper(componentModel = "spring", imports = Formatter.class)
public interface CommentMapper {

    @Mapping(target = "created", expression = "java(Formatter.format(comment.getCreated()))")
    @Mapping(target = "updated", expression = "java(comment.getUpdated() == null ? null : Formatter.format(comment.getUpdated()))")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    Comment toComment(CommentShortDto dto);
}
