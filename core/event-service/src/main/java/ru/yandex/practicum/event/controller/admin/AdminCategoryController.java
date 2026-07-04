package ru.yandex.practicum.event.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.event.category.CategoryShortDto;

public interface AdminCategoryController {

    /**
     * Creates a new category.
     *
     * @param request DTO containing category details
     * @return created category DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto addCategory(@RequestBody @Valid CategoryShortDto request);

    /**
     * Updates an existing category.
     *
     * @param catId   id of the category to update
     * @param request DTO containing the updated category details
     * @return updated category DTO
     */
    @PatchMapping("/{catId}")
    CategoryDto patchCategory(@PathVariable @Positive Long catId,
                              @RequestBody @Valid CategoryShortDto request);

    /**
     * Deletes a category by its id.
     * The category must not be linked to any existing event.
     *
     * @param catId id of the category to delete
     */
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable @Positive Long catId);
}
