package ru.yandex.practicum.event.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.category.CategoryService;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class PublicCategoryControllerImpl implements PublicCategoryController {

    private final CategoryService categoryService;

    @Override
    public List<CategoryDto> findAll(Integer from,
                                     Integer size) {
        return categoryService.findAll(from, size);
    }

    @Override
    public CategoryDto findById(Long catId) {
        return categoryService.findById(catId);
    }
}
