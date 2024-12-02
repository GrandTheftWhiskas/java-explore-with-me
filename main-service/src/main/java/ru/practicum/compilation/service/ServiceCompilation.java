package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.dto.CompilationUpdate;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceCompilation {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CompilationResponse add(CompilationDto dto) {
        if (dto.getPinned() == null) {
            dto.setPinned(false);
        }

        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());
        if (dto.getEvents() == null) {
            compilation.setEvents(new ArrayList<>());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(dto.getEvents()));
        }

        return CompilationMapper.toCompilationResponse(compilationRepository.save(compilation));
    }

    @Transactional
    public CompilationResponse update(CompilationUpdate dto, Long id) {
        System.out.println(dto);
        if (compilationRepository.existsById(id)) {
            Compilation compilation = compilationRepository.findCompilationById(id);
            if (dto.getTitle() != null) {
                compilation.setTitle(dto.getTitle());
            } else if (dto.getPinned() != null) {
                compilation.setPinned(dto.getPinned());
            }
            compilationRepository.save(compilation);
            List<Event> events;
            if (dto.getEvents() == null) {
                events = List.of();
            } else {
                events = eventRepository.findByIdIn(dto.getEvents());
            }
            compilation.setEvents(events);
            return CompilationMapper.toCompilationResponse(compilation);
        } else {
            throw new NotFoundException("Подборка в методе на обновление не найдена");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (compilationRepository.existsById(id)) {
            compilationRepository.deleteById(id);
        } else {
            throw new NotFoundException("Подборка не найдена");
        }
    }

    public List<CompilationResponse> getAll(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAll(pageable).stream().toList();

        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationResponse(compilation)).toList();
    }

    public CompilationResponse get(Long id) {
        Compilation compilation = compilationRepository.findCompilationById(id);
        if (compilation == null) {
            throw new NotFoundException("Подборка не найдена");
        }

        return CompilationMapper.toCompilationResponse(compilation);
    }

}
