package ru.yandex.practicum.event.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.category.CategoryService;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.event.category.CategoryShortDto;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryControllerImpl implements AdminCategoryController {

    private final CategoryService categoryService;

    @Override
    public CategoryDto addCategory(CategoryShortDto request) {
        return categoryService.addCategory(request);
    }

    @Override
    public CategoryDto patchCategory(Long catId,
                                     CategoryShortDto request) {
        return categoryService.patchCategory(catId, request);
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryService.deleteCategory(catId);
    }
}
