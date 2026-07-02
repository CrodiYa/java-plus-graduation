package ru.yandex.practicum.event.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.compilation.CompilationService;
import ru.yandex.practicum.interaction.dto.event.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.UpdateCompilationDto;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationControllerImpl implements AdminCompilationController {

    private final CompilationService compilationService;

    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {
        return compilationService.addCompilation(dto);
    }

    @Override
    public CompilationDto updateCompilation(Long compId,
                                            UpdateCompilationDto dto) {
        return compilationService.updateCompilation(compId, dto);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationService.deleteCompilation(compId);
    }
}
