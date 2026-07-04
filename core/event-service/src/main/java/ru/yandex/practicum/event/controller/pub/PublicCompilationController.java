package ru.yandex.practicum.event.controller.pub;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.dto.event.compilation.CompilationDto;

import java.util.List;

public interface PublicCompilationController {

    /**
     * Retrieves a paginated list of compilations, optionally filtered by pinned status.
     *
     * @param pinned flag to filter compilations by pinned status (optional)
     * @param from   the index of the first element to retrieve (0-based), default is 0
     * @param size   the number of elements to retrieve, default is 10
     * @return list of compilation DTOs
     */
    @GetMapping
    List<CompilationDto> findCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size);

    /**
     * Retrieves detailed information about a specific compilation by its id.
     *
     * @param compId id of the compilation to retrieve
     * @return compilation DTO
     */
    @GetMapping("/{compId}")
    CompilationDto findCompilationById(@PathVariable @Positive Long compId);
}
