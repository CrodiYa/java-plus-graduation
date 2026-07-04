package ru.yandex.practicum.event.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.event.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.UpdateCompilationDto;

public interface AdminCompilationController {

    /**
     * Creates a new compilation of events.
     *
     * @param dto DTO containing compilation details and list of event IDs
     * @return created compilation DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto dto);

    /**
     * Updates an existing compilation.
     *
     * @param compId id of the compilation to update
     * @param dto    DTO containing the fields to update
     * @return updated compilation DTO
     */
    @PatchMapping("/{compId}")
    CompilationDto updateCompilation(@PathVariable @Positive Long compId,
                                     @Valid @RequestBody UpdateCompilationDto dto);

    /**
     * Deletes a compilation by its id.
     *
     * @param compId id of the compilation to delete
     */
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCompilation(@PathVariable @Positive Long compId);
}
