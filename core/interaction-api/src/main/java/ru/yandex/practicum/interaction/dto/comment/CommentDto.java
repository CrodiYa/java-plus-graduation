package ru.yandex.practicum.interaction.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long eventId;
    private Long authorId;
    private String authorName;
    private String text;
    private String created;
    private String updated;
}
