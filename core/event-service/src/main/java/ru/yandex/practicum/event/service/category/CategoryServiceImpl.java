package ru.yandex.practicum.event.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.mapper.CategoryMapper;
import ru.yandex.practicum.event.model.category.Category;
import ru.yandex.practicum.event.repository.CategoryRepository;
import ru.yandex.practicum.interaction.dto.event.category.CategoryDto;
import ru.yandex.practicum.interaction.dto.event.category.CategoryShortDto;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(Integer from, Integer size) {
        int page = from / size;

        return categoryRepository.findAll(PageRequest.of(page, size)).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = findEntityById(id);

        return categoryMapper.toDto(category);
    }

    @Override
    public Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));
    }


    @Override
    public CategoryDto addCategory(CategoryShortDto request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Name is not unique");
        }

        Category category = categoryRepository.save(categoryMapper.toCategory(request));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto patchCategory(Long id, CategoryShortDto request) {
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ConflictException("Name is not unique");
        }

        Category category = findEntityById(id);
        categoryMapper.merge(category, request);

        Category patched = categoryRepository.save(category);

        return categoryMapper.toDto(patched);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        try {
            throwIfCategoryNotFound(id);

            categoryRepository.deleteById(id);
            categoryRepository.flush();

        } catch (DataIntegrityViolationException e) {
            log.debug("Conflict during deleting category with id [{}]", id, e);
            throw new ConflictException("Some events have this category");
        }
    }

    @Override
    public void throwIfCategoryNotFound(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with id " + categoryId + " not found");
        }
    }
}
