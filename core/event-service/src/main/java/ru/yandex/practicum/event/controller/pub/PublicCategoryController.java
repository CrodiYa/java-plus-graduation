package ru.yandex.practicum.event.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;

import java.util.List;

public interface PublicCategoryController {

    /**
     * Retrieves a paginated list of all categories.
     *
     * @param from the index of the first element to retrieve (0-based), default is 0
     * @param size the number of elements to retrieve, default is 10
     * @return list of category DTOs
     */
    @GetMapping
    List<CategoryDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                              @RequestParam(defaultValue = "10") @Positive Integer size);

    /**
     * Retrieves detailed information about a specific category by its id.
     *
     * @param catId id of the category to retrieve
     * @return category DTO
     */
    @GetMapping("/{catId}")
    CategoryDto findById(@PathVariable @Positive Long catId);
}
