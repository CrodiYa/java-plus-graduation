package ru.yandex.practicum.ewm.mappers;

import org.mapstruct.*;
import ru.yandex.practicum.ewm.model.category.Category;
import ru.yandex.practicum.ewm.model.category.CategoryDtoRequest;
import ru.yandex.practicum.ewm.model.category.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryDtoRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void merge(@MappingTarget Category category, CategoryDtoRequest dto);
}
