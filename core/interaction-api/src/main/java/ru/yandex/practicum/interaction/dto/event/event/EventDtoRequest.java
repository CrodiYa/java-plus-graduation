package ru.yandex.practicum.interaction.dto.event.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.common.Formatter;
import ru.yandex.practicum.interaction.validation.NullNotBlank;
import ru.yandex.practicum.interaction.validation.OnCreate;
import ru.yandex.practicum.interaction.validation.OnUpdate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDtoRequest {

    @NotBlank(groups = OnCreate.class, message = "Title can`t be blank")
    @NullNotBlank(groups = OnUpdate.class, message = "Title can`t be blank")
    @Size(groups = {OnCreate.class, OnUpdate.class},
            min = 3, max = 120,
            message = "Title can`t be less than 3 and longer than 120 characters")
    private String title;

    @NotBlank(groups = OnCreate.class, message = "Annotation can`t be blank")
    @NullNotBlank(groups = OnUpdate.class, message = "Annotation can`t be blank")
    @Size(groups = {OnCreate.class, OnUpdate.class},
            min = 20, max = 2000,
            message = "Annotation can`t be less than 20 and longer than 2000 characters")
    private String annotation;

    @NotBlank(groups = OnCreate.class, message = "Description can`t be blank")
    @NullNotBlank(groups = OnUpdate.class, message = "Description can`t be blank")
    @Size(groups = {OnCreate.class, OnUpdate.class},
            min = 20, max = 7000,
            message = "Description can`t be less than 20 and longer than 7000 characters")
    private String description;

    @Valid
    @NotNull(groups = OnCreate.class)
    private Location location;

    @NotNull(groups = OnCreate.class)
    @JsonFormat(pattern = Formatter.PATTERN)
    private String eventDate;

    @NotNull(groups = OnCreate.class)
    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Long category;

    @PositiveOrZero(groups = {OnCreate.class, OnUpdate.class})
    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;

    @Null(groups = OnCreate.class)
    private EventStateAction stateAction;
}
