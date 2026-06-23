package ru.yandex.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.stats.server.model.EndpointHit;
import ru.yandex.practicum.interaction.common.Formatter;
import ru.yandex.practicum.interaction.stats.dto.EndpointHitDto;

@Mapper(componentModel = "spring", imports = Formatter.class)
public interface DtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", expression = "java(Formatter.toInstant(dto.getTimestamp()))")
    EndpointHit toEndpoint(EndpointHitDto dto);

    @Mapping(target = "timestamp", expression = "java(Formatter.format(hit.getCreateDate()))")
    EndpointHitDto toEndpointDto(EndpointHit hit);
}
