package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDto;
import ru.practicum.client.StatClient;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventIdByRequestsCount;
import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServicePublic {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EventRespShort> search(String text, List<Integer> categories, Boolean paid,
                                       LocalDateTime start, LocalDateTime end,
                                       Boolean available, String sort, int from, int size) {
        System.out.println("Сервис");
            if (start != null && end != null) {
                if (!start.isBefore(end)) {
                    throw new BadRequestException("Неверная дата");
                }
            }

            if (text == null) {
                text = "";
            } else if (categories == null) {
                categories = List.of();
            } else if (start == null) {
                start = LocalDateTime.now();
            } else if (end == null) {
                end = LocalDateTime.parse("3000-12-12 12:12:12", formatter);
            }

            Pageable pageable = PageRequest.of(from, size);
            List<EventRespShort> events;
                if (start != null && end != null) {
                    events = eventRepository.searchEvents(text, categories, paid,
                                    start, end, available, size).stream()
                            .map(event -> EventMapper.toRespShort(event)).toList();
                } else {
                    events = eventRepository.findAllByCategories(categories, pageable).stream()
                            .map(event -> EventMapper.toRespShort(event)).toList();
                }
                System.out.println(events);
            List<Long> ids = events.stream().map(eventRespShort -> eventRespShort.getId()).toList();
                System.out.println(122);
            Map<Integer, Integer> confirmedRequests = requestRepository
                    .countByEventIdInAndStatusGroupByEvent(ids, "CONFIRMED").stream()
                    .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));
            System.out.println(confirmedRequests);
            String uris = ids.stream().map((id) -> "/event/" + id).collect(Collectors.joining());
            ResponseEntity<List<StatsDto>> response = statClient.getStat(
                    LocalDateTime.parse("1000-12-12 12:12:12", formatter),
                    LocalDateTime.parse("3000-12-12 12:12:12", formatter), uris, true);
            List<StatsDto> statResponse = response.getBody();
            List<Long> views = statResponse.stream().map(statsDto -> statsDto.getHits()).toList();
            for (int i = 0; i < events.size(); i++) {
                if ((!views.isEmpty()) && (views.get(i) != 0)) {
                    events.get(i).setViews(views.get(i));
                } else {
                    events.get(i).setViews(0L);
                }
                events.get(i)
                        .setConfirmedRequests(confirmedRequests
                                .getOrDefault(events.get(i).getId(), 0));

            }

            return events;
    }

    public EventRespShort get(Long eventId, String path) {
        try {
            System.out.println("сервис2");
            Event event = eventRepository.findByIdAndState(eventId, "PUBLISHED");
            Long requestConfirm = requestRepository.countByEventIdAndStatus(eventId, "CONFIRMED");
            System.out.println("сервис2");
            EventRespShort full = EventMapper.toRespShort(event);
            System.out.println("сервис2");
            full.setConfirmedRequests(requestConfirm);
            ResponseEntity<List<StatsDto>> response =
                    statClient.getStat(LocalDateTime.parse("1000-12-12 12:12:12", formatter),
                    LocalDateTime.parse("3000-12-12 12:12:12", formatter), path, true);
            List<StatsDto> statResponse = response.getBody();
            List<Long> views = statResponse.stream().map(statsDto -> statsDto.getHits()).toList();
            if (views.isEmpty()) {
                full.setViews(0L);
            } else {
                full.setViews(views.get(0));
            }
            return full;
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }
}
