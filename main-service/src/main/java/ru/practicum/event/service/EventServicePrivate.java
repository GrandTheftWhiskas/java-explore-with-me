package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.Update;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventIdByRequestsCount;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestForConfirmation;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServicePrivate {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatClient statClient;

    public EventDto add(EventDto dto, Long userId) {
        if (dto.getRequestModeration() == null) {
            dto.setRequestModeration(true);
        } else if (dto.getParticipantLimit() < 0) {
            throw new BadRequestException("Лимит участников не может быть меньше нуля");
        } else if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата не может быть раньше 2 часов");
        }

        locationRepository.save(dto.getLocation());
        Event event = EventMapper.toEvent(dto);
        event.setInit(userRepository.getUserById(userId));
        event.setCategory(categoryRepository.findCategoryById(dto.getCategory()));
        event.setCreated(LocalDateTime.now());
        event.setState("PENDING");
        return EventMapper.toEventDto(eventRepository.save(event));
    }

    public List<EventRespShort> get(Long userId, int from, int size) {
        try {
            List<EventRespShort> events = eventRepository.findByInitId(userId, size).stream()
                    .map(event -> EventMapper.toRespShort(event)).toList();
            List<Long> ids = events.stream().map(eventRespShort -> eventRespShort.getId()).toList();
            Map<Integer, Integer> requestsConfirm =
                    requestRepository.countByEventIdInAndStatusGroupByEvent(ids, "PUBLISHED").stream()
                            .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));
            String uris = ids.stream().map((id) -> "/event/" + id).collect(Collectors.joining());
            ResponseEntity<List<StatsDto>> response =
                    statClient.getStat(LocalDateTime.parse("1000-12-12 12:12:12", formatter),
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
                        .setConfirmedRequests(requestsConfirm
                                .getOrDefault(events.get(i).getId(), 0));
            }

            return events;
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }

    public EventRespShort getFull(Long userId, Long eventId, String path) {
        try {
        Event event = eventRepository.getEventById(eventId);
        System.out.println(event);
        Long requestConfirm  = requestRepository.countByEventIdAndStatus(eventId, "PUBLISHED");
        EventRespShort eventRespShort = EventMapper.toRespShort(event);
        System.out.println(eventRespShort);
        eventRespShort.setConfirmedRequests(requestConfirm);
        ResponseEntity<List<StatsDto>> response =
                statClient.getStat(LocalDateTime.parse("1000-12-12 12:12:12", formatter),
                LocalDateTime.parse("3000-12-12 12:12:12", formatter), path, true);
        List<StatsDto> statResponse = response.getBody();
        List<Long> views = statResponse.stream().map(statsDto -> statsDto.getHits()).toList();
        if (views.isEmpty()) {
            eventRespShort.setViews(0L);
            return eventRespShort;
        }
        eventRespShort.setViews(views.getFirst());
            System.out.println(eventRespShort);
        return eventRespShort;
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }

    public Map<String, List<RequestDto>> approve(RequestForConfirmation requestFor, Long userId, Long eventId) {
            Event event = eventRepository.getEventById(eventId);
            List<Request> requests = requestRepository.findByIdInAndEventId(requestFor.getRequestIds(), eventId);
            Long participants;

            for (Request request1 : requests) {
                participants = requestRepository.countByEventIdAndStatus(eventId, "CONFIRMED");
                if (!request1.getStatus().equals("PENDING")) {
                    throw new ValidationException("Неверно указан статус");
                } else if (event.getLimit() < participants) {
                    throw new ValidationException("Превышен лимит");
                } else if (requestFor.getStatus().equals("CONFIRMED")) {
                    request1.setStatus("CONFIRMED");
                } else if (requestFor.getStatus().equals("REJECTED")) {
                    request1.setStatus("REJECTED");
                }
                System.out.println(request1.getStatus());
                requestRepository.save(request1);
            }
                if (requestFor.getStatus().equals("REJECTED")) {
                    return Map.of("rejectedRequests",
                            requests.stream().map(request -> RequestMapper.toRequestDto(request)).toList());
                } else {
                    return Map.of("confirmedRequests",
                            requests.stream().map(request -> RequestMapper.toRequestDto(request)).toList());
                }
    }

    public EventDto update(EventUpdate eventUpdate, Long userId, Long eventId) {
        Event event = eventRepository.getEventById(eventId);
        if (eventUpdate.getParticipantLimit() < 0) {
            throw new BadRequestException("Попытка изменить лимит на отрицательный");
        } else if (!(event.getState().equals("PENDING")) && !(event.getState().equals("CANCELED"))) {
            throw new ValidationException("Неверно указан статус");
        } else if (eventUpdate.getEventDate() != null) {
            if (eventUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата не может быть раньше 2 часов");
            }
        }

        Category category = event.getCategory();
        if (eventUpdate.getCategory() != null) {
            category = categoryRepository.findCategoryById(eventUpdate.getCategory());
        }

        Event result = Update.updateEvent(event, eventUpdate, category);
        return EventMapper.toEventDto(result);
    }

    public List<RequestDto> get(Long userId, Long eventId) {
        try {
            return requestRepository.findByEventId(eventId).stream()
                    .map(request -> RequestMapper.toRequestDto(request)).toList();
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }

    public List<Request> doStatus(List<Request> requests, String status) {
        for (Request request : requests) {
            request.setStatus(status);
        }
        return requests;
    }
}
