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
public class EventShortDto {

    private Long id;
    private String title;
    private String annotation;
    private String eventDate;
    private CategoryDto category;
    private UserDto initiator;
    private Boolean paid;
    private Integer confirmedRequests;
    private Double rating;
}
