package ru.yandex.practicum.ewm.mappers;

import org.mapstruct.*;
import ru.yandex.practicum.ewm.model.compilation.Compilation;
import ru.yandex.practicum.ewm.model.compilation.CompilationDto;
import ru.yandex.practicum.ewm.model.compilation.NewCompilationDto;
import ru.yandex.practicum.ewm.model.compilation.UpdateCompilationRequest;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toEntity(NewCompilationDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    void merge(@MappingTarget Compilation compilation, UpdateCompilationRequest dto);
}
