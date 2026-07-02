package ru.yandex.practicum.event.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.service.compilation.CompilationService;
import ru.yandex.practicum.interaction.dto.event.compilation.CompilationDto;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationControllerImpl implements PublicCompilationController {

    private final CompilationService compilationService;

    @Override
    public List<CompilationDto> findCompilations(
            Boolean pinned,
            Integer from,
            Integer size) {
        return compilationService.findCompilations(pinned, from, size);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        return compilationService.findCompilationById(compId);
    }
}
