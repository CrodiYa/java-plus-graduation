package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.event.model.compilation.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findByPinned(Boolean pinned, PageRequest pageRequest);
}
