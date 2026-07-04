package ru.yandex.practicum.interaction.dto.event.compilation;

import lombok.*;
import ru.yandex.practicum.interaction.dto.event.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}
