package ru.yandex.practicum.event.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.mapper.CompilationMapper;
import ru.yandex.practicum.event.model.compilation.Compilation;
import ru.yandex.practicum.event.model.event.Event;
import ru.yandex.practicum.event.repository.CompilationRepository;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.interaction.dto.event.compilation.CompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.NewCompilationDto;
import ru.yandex.practicum.interaction.dto.event.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {
        Set<Event> events = getEventsFromIds(dto.getEvents());

        Compilation compilation = compilationMapper.toEntity(dto);
        compilation.setEvents(events);

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest dto) {
        Compilation compilation = findEntityById(compilationId);

        compilationMapper.merge(compilation, dto);

        if (dto.getEvents() != null) {
            compilation.setEvents(getEventsFromIds(dto.getEvents()));
        }

        return compilationMapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Compilation with id " + compilationId + " not found");
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }

        return compilations.stream()
                .map(compilationMapper::toDto)
                .toList();
    }

    @Override
    public CompilationDto findCompilationById(Long compilationId) {
        return compilationMapper.toDto(findEntityById(compilationId));
    }

    private Compilation findEntityById(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compilationId + " not found"));
    }

    private Set<Event> getEventsFromIds(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(eventRepository.findAllById(eventIds));
    }
}
