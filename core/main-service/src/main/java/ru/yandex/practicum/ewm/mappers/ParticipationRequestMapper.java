package ru.yandex.practicum.ewm.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.ewm.model.participation.ParticipationRequest;
import ru.yandex.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.yandex.practicum.interaction.common.Formatter;

@Mapper(componentModel = "spring", imports = Formatter.class)
public interface ParticipationRequestMapper {

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    @Mapping(target = "created", expression = "java(Formatter.format(participationRequest.getCreated()))")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);
}
