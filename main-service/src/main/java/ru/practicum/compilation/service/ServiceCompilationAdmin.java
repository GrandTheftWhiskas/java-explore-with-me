package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompositeKeyForEventByComp;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.repository.EventByCompilationRepository;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceCompilationAdmin {
    private final CompilationRepository compilationRepository;
    private final EventByCompilationRepository repository;
    private final EventRepository eventRepository;

    @Transactional
    public CompilationResponse add(CompilationDto dto) {
        System.out.println(dto);
        if (dto.getPinned() == null) {
            dto.setPinned(false);
        }

        Compilation compilation =
                compilationRepository.save(new Compilation(dto.getId(), dto.getTitle(), dto.getPinned()));
        CompilationResponse response = new CompilationResponse();
        response.setId(compilation.getId());
        response.setTitle(compilation.getTitle());
        response.setPinned(compilation.getPinned());
        if (dto.getEvents() == null) {
            response.setEvents(new ArrayList<>());
            return response;
        }

        response.setEvents(addEventByCompilations(dto, compilation.getId()));
        return response;
    }

    @Transactional
    public CompilationResponse update(CompilationDto dto, Long id) {

        Compilation compilation = compilationRepository.findCompilationById(id);
        Compilation newCompilation = new Compilation(dto.getId(), dto.getTitle(), dto.getPinned());
        System.out.println(newCompilation);
        if (newCompilation.getTitle() != null) {
            if (newCompilation.getTitle().length() > 50) {
                throw new BadRequestException("Длина текста не должна превышать 50");
            }
            compilation.setTitle(newCompilation.getTitle());
        } else if (newCompilation.getPinned() != null) {
            compilation.setPinned(newCompilation.getPinned());
        }

        CompilationResponse response = CompilationMapper.toCompilationResponse(compilation);
        if (dto.getEvents() == null) {
            response.setEvents(List.of());
            return response;
        }

        response.setEvents(addEventByCompilations(dto, id));
        return response;
}


    @Transactional
    public void delete(Long id) {
        if (compilationRepository.findCompilationById(id) == null) {
            throw new ValidationException("Подборка не найдена");
        }
        compilationRepository.deleteById(id);
        repository.deleteById(id);

    }

    @Transactional
    private List<EventRespShort> addEventByCompilations(CompilationDto compilation, Long id) {

        List<CompositeKeyForEventByComp> eventsByComp = compilation
                .getEvents()
                .stream()
                .map((EbCId) -> new CompositeKeyForEventByComp(id, EbCId))
                .toList();

        repository.saveAll(eventsByComp);

        return eventRepository.findByIdIn(compilation.getEvents())
                .stream()
                .map(event -> EventMapper.toRespShort(event))
                .toList();
    }
}
