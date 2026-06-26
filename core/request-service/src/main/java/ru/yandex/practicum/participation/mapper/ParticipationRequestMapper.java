package ru.yandex.practicum.participation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.Formatter;
import ru.yandex.practicum.interaction.dto.participation.ParticipationRequestDto;
import ru.yandex.practicum.participation.model.ParticipationRequest;

@Mapper(componentModel = "spring", imports = Formatter.class)
public interface ParticipationRequestMapper {

    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "requesterId", target = "requester")
    @Mapping(target = "created", expression = "java(Formatter.format(participationRequest.getCreated()))")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);
}
