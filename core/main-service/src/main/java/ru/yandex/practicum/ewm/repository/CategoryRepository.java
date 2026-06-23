package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
