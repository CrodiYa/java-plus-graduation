package ru.yandex.practicum.event.service.category;

import ru.yandex.practicum.event.model.category.Category;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDtoRequest;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.List;

public interface CategoryService {

    /**
     * Retrieves a paginated list of all categories.
     *
     * @param from the index of the first element to retrieve
     * @param size the number of elements to retrieve
     * @return list of category DTOs
     */
    List<CategoryDto> findAll(Integer from, Integer size);

    /**
     * Retrieves detailed information about a specific category by its id.
     *
     * @param id id of the category to retrieve
     * @return category DTO
     * @throws NotFoundException if the category with the given id does not exist
     */
    CategoryDto findById(Long id);

    /**
     * Retrieves a category entity by its id.
     *
     * @param id id of the category to retrieve
     * @return category entity
     * @throws NotFoundException if the category with the given id does not exist
     */
    Category findEntityById(Long id);

    /**
     * Creates a new category.
     *
     * @param request DTO containing category details
     * @return created category DTO
     * @throws ConflictException if a category with the same name already exists
     */
    CategoryDto addCategory(CategoryDtoRequest request);

    /**
     * Updates an existing category.
     *
     * @param id      id of the category to update
     * @param request DTO containing the fields to update
     * @return updated category DTO
     * @throws NotFoundException if the category with the given id does not exist
     * @throws ConflictException if a category with the same name already exists
     */
    CategoryDto patchCategory(Long id, CategoryDtoRequest request);

    /**
     * Deletes a category by its id.
     * The category must not be linked to any existing event.
     *
     * @param id id of the category to delete
     * @throws NotFoundException if the category with the given id does not exist
     * @throws ConflictException if the category is referenced by an event
     */
    void deleteCategory(Long id);

    /**
     * Checks if a category with the specified id exists.
     * Throws an exception if the category is not found.
     *
     * @param categoryId id of the category to check
     * @throws NotFoundException if the category with the given id does not exist
     */
    void throwIfCategoryNotFound(Long categoryId);
}
