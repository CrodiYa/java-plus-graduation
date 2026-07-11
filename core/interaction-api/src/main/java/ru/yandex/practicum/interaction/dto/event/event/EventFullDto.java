package ru.yandex.practicum.interaction.dto.event.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.user.UserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private Location location;
    private String eventDate;

    private CategoryDto category;
    private UserDto initiator;

    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    private EventState state;
    private String publishedOn;
    private String createdOn;

    private Integer confirmedRequests;
    private Double rating;
}
