package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationResponse;
import ru.practicum.compilation.dto.EventByCompId;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.repository.EventByCompilationRepository;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceCompilationPublic {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventByCompilationRepository repository;

    public List<CompilationResponse> getAll(boolean pinned, int from, int size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Введены некорректные значения");
        }

        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.findAll(pageable).stream().toList();
        List<EventByCompId> eventByCompIds = repository.findEventsByCompIdIn(compilations.stream()
                .map(compilation -> compilation.getId()).toList());
        Map<Long, List<EventRespShort>> eventShortListByCompId = new HashMap<>();

        for (EventByCompId eventByCompId : eventByCompIds) {
            if (!eventShortListByCompId.containsKey(eventByCompId.getCompilationId())) {
                Event event = eventByCompId.getEvent();
                List<EventRespShort> events;
                if (event != null) {
                    events = new ArrayList<>();
                    events.add(EventMapper.toRespShort(eventByCompId.getEvent()));
                } else {
                    events = List.of();
                }
                eventShortListByCompId.put(eventByCompId.getCompilationId(), events);
                continue;
            }
            if (eventByCompId.getEvent() == null) {
                continue;
            }
            eventShortListByCompId.get(eventByCompId.getCompilationId())
                    .add(EventMapper.toRespShort(eventByCompId.getEvent()));
        }

        List<CompilationResponse> compilationResponses = new ArrayList<>();

        for (Compilation compilation : compilations) {
            List<EventRespShort> events = eventShortListByCompId.get(compilation.getId());
            if (events == null) {
                events = List.of();
            }
            CompilationResponse compilationResponse = CompilationMapper.toCompilationResponse(compilation);
            compilationResponse.setEvents(events);
            compilationResponses.add(compilationResponse);
        }
        return compilationResponses;
    }

    public CompilationResponse get(Long id) {
        Compilation compilation = compilationRepository.findCompilationById(id);
        if (compilation == null) {
            throw new NotFoundException("Подборка не найдена");
        }

        List<Long> ids = repository.findByCompilationId(id);
        List<EventRespShort> eventList = eventRepository.findByIdIn(ids).stream()
                .map(event -> EventMapper.toRespShort(event)).toList();
        CompilationResponse response = CompilationMapper.toCompilationResponse(compilation);
        response.setEvents(eventList);
        return response;
    }
}
