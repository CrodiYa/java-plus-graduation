package ru.yandex.practicum.event.service.compilation;

import ru.yandex.practicum.interaction.dto.event.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.UpdateCompilationDto;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.List;

public interface CompilationService {

    /**
     * Creates a new compilation of events.
     *
     * @param dto DTO containing compilation details and list of event IDs
     * @return created compilation DTO
     */
    CompilationDto addCompilation(NewCompilationDto dto);

    /**
     * Updates an existing compilation.
     * Allows modifying the title, pinned status, and the list of events.
     *
     * @param compilationId id of the compilation to update
     * @param dto           DTO containing the fields to update
     * @return updated compilation DTO
     * @throws NotFoundException if the compilation or any referenced event does not exist
     */
    CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto dto);

    /**
     * Deletes a compilation by its id.
     *
     * @param compilationId id of the compilation to delete
     * @throws NotFoundException if the compilation with the given id does not exist
     */
    void deleteCompilation(Long compilationId);

    /**
     * Retrieves a paginated list of compilations, optionally filtered by pinned status.
     *
     * @param pinned flag to filter compilations by pinned status
     * @param from   the index of the first element to retrieve
     * @param size   the number of elements to retrieve
     * @return list of compilation DTOs
     */
    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);

    /**
     * Retrieves detailed information about a specific compilation by its id.
     *
     * @param compilationId id of the compilation to retrieve
     * @return compilation DTO
     * @throws NotFoundException if the compilation with the given id does not exist
     */
    CompilationDto findCompilationById(Long compilationId);
}
