package ru.practicum.event.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventIdByRequestsCount;
import ru.practicum.client.StatClient;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.Update;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceAdmin {
    private int index = 0;
    private int oneMoreIndex = 0;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatClient statClient;


    public EventRespShort update(EventUpdate eventUpdate, Long id) {
        System.out.println("Обнова");
        System.out.println(eventUpdate);
        try {
        Event event = eventRepository.getEventById(id);
        if (event.getState().equals("PUBLISHED") || event.getState().equals("CANCELED")) {
            throw new ValidationException("Текущий статус не подходит");
        } else if (eventUpdate.getEventDate() != null) {
            if (eventUpdate.getEventDate().isBefore(LocalDateTime.now().plusMinutes(2))) {
                throw new BadRequestException("Указана неправильная дата");
            }
        }

        Category category = event.getCategory();
        if (eventUpdate.getCategory() != null) {
            category = categoryRepository.findCategoryById(eventUpdate.getCategory());
        }

        if (eventUpdate.getLocation() != null) {
            locationRepository.save(eventUpdate.getLocation());
        }

        Event newEvent = Update.updateEvent(event, eventUpdate, category);
        eventRepository.save(event);
        Long requests = requestRepository.countByEventIdAndStatus(id, "CONFIRMED");
        EventRespShort result = EventMapper.toRespShort(newEvent);
        result.setConfirmedRequests(requests);
        return result;
        } catch (NullPointerException e) {
            throw new NotFoundException("Объект не найден");
        }
    }

    public List<EventRespShort> get(List<Long> users, List<String> states, List<Integer> categories,
                                   LocalDateTime start, LocalDateTime end, int from, int size) {
        try {
            if (users == null) {
                users = List.of();
            } else if (states == null) {
                states = List.of();
            } else if (categories == null) {
                categories = List.of();
            } else if (start == null) {
                start = LocalDateTime.parse("1000-12-12 12:12:12", formatter);
            } else if (end == null) {
                end = LocalDateTime.parse("3000-12-12 12:12:12", formatter);
            } else if (start.isAfter(end)) {
                throw new ValidationException("Указана неверная дата");
            }

            Pageable pageable = PageRequest.of(from, size);
            List<EventRespShort> eventRespFulls;
                if (start != null && end != null) {
                    eventRespFulls = eventRepository
                            .findByConditionals(states, categories, users, start, end, size)
                            .stream()
                            .map(event -> EventMapper.toRespShort(event))
                            .toList();
                } else if (categories != null) {
                    eventRespFulls = eventRepository.findAllByCategories(categories, pageable).stream()
                            .map((event -> EventMapper.toRespShort(event))).toList();
                } else {
                    eventRespFulls = eventRepository.findAll(size).stream()
                            .map((event -> EventMapper.toRespShort(event))).toList();
                }
            System.out.println(eventRespFulls);
            List<Long> eventsIds = eventRespFulls
                    .stream()
                    .map(EventRespShort::getId)
                    .toList();

            Map<Integer, Integer> requestsConfirm = requestRepository
                    .countByEventIdInAndStatusGroupByEvent(eventsIds, "PUBLISHED")
                    .stream()
                    .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));
            System.out.println(requestsConfirm);
            String uris = eventsIds.stream().map((id) -> "/event/" + id).collect(Collectors.joining());
            ResponseEntity<List<StatsDto>> response =
                    statClient.getStat(LocalDateTime.parse("1000-12-12 12:12:12", formatter),
                            LocalDateTime.parse("3000-12-12 12:12:12", formatter), uris, true);
            List<StatsDto> statResponse = response.getBody();
            List<Long> views = statResponse.stream().map(statsDto -> statsDto.getHits()).toList();
            for (int i = 0; i < eventRespFulls.size(); i++) {
                if ((!views.isEmpty()) && (views.get(i) != 0)) {
                    eventRespFulls.get(i).setViews(views.get(i));
                } else {
                    eventRespFulls.get(i).setViews(0L);
                }
                eventRespFulls.get(i)
                        .setConfirmedRequests(requestsConfirm
                                .getOrDefault(eventRespFulls.get(i).getId(), index));
                oneMoreIndex++;
                if (oneMoreIndex % 2 == 0) {
                    index--;
                } else {
                    index++;
                }
            }
            return eventRespFulls;
        } catch (NullPointerException e) {
            throw new NotFoundException("Объект не найден");
        }
    }
}
