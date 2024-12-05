package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.dto.EventDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatClient;
import ru.practicum.event.dto.EventRespShort;
import ru.practicum.event.dto.EventUpdate;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.model.Status;
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
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private int defaultValue = 0;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final CommentRepository commentRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatClient statClient;

    //AdminService
    public EventRespShort update(EventUpdate eventUpdate, Long id) {
        try {
            Event event = eventRepository.getEventById(id);
            if (event.getState().equals(String.valueOf(Status.PUBLISHED))
                    || event.getState().equals(String.valueOf(Status.CANCELED))) {
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

            Event newEvent = updateEvent(event, eventUpdate, category);
            eventRepository.save(event);
            Long requests = requestRepository.countByEventIdAndStatus(id, String.valueOf(RequestStatus.CONFIRMED));
            EventRespShort result = EventMapper.toRespShort(newEvent);
            result.setConfirmedRequests(requests);
            result.setComments(giveComments(result.getId()));
            return result;
        } catch (NullPointerException e) {
            throw new NotFoundException("Объект не найден");
        }
    }

    public List<EventRespShort> get(List<Long> users, List<String> states, List<Integer> categories,
                                    LocalDateTime start, LocalDateTime end, int from, int size) {
        if (users == null) {
            users = List.of();
        }

        if (states == null) {
            states = List.of();
        }

        List<EventRespShort> events;
        Pageable pageable = PageRequest.of(from / size, size);
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new ValidationException("Указана неверная дата");
            }
            events = eventRepository
                    .findByConditionals(states, categories, users, start, end, pageable)
                    .stream()
                    .map(event -> EventMapper.toRespShort(event))
                    .toList();
        } else if (categories != null) {
            events = eventRepository.findAllByCategories(categories, pageable).stream()
                    .map((event -> EventMapper.toRespShort(event))).toList();
        } else {
            events = eventRepository.findAll(pageable).stream()
                    .map((event -> EventMapper.toRespShort(event))).toList();
        }
        List<Long> eventsIds = events
                .stream()
                .map(EventRespShort::getId)
                .toList();
        Map<Integer, Integer> requestsConfirm = requestRepository
                .countByEventIdInAndStatusGroupByEvent(eventsIds, String.valueOf(Status.PUBLISHED))
                .stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));
        String uris = eventsIds.stream().map((id) -> "/event/" + id).collect(Collectors.joining());
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
                            .getOrDefault(events.get(i).getId(), defaultValue));
            events.get(i).setComments(giveComments(events.get(i).getId()));
            defaultValue++;
        }
        return events;
    }

    //PrivateService
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
        event.setState(String.valueOf(Status.PENDING));
        EventDto eventDto = EventMapper.toEventDto(eventRepository.save(event));
        eventDto.setComments(giveComments(eventDto.getId()));
        return eventDto;
    }

    public List<EventRespShort> get(Long userId, int from, int size) {
        try {
            Pageable pageable = PageRequest.of(from / size, size);
            List<EventRespShort> events = eventRepository.findByInitId(userId, pageable).stream()
                    .map(event -> EventMapper.toRespShort(event)).toList();
            List<Long> ids = events.stream().map(eventRespShort -> eventRespShort.getId()).toList();
            Map<Integer, Integer> requestsConfirm = requestRepository
                    .countByEventIdInAndStatusGroupByEvent(ids, String.valueOf(RequestStatus.CONFIRMED)).stream()
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
                events.get(i).setComments(giveComments(events.get(i).getId()));
            }

            return events;
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }

    public EventRespShort getFull(Long userId, Long eventId, String path) {
        try {
            Event event = eventRepository.getEventById(eventId);
            Long requestConfirm  = requestRepository
                    .countByEventIdAndStatus(eventId, String.valueOf(RequestStatus.CONFIRMED));
            EventRespShort eventRespShort = EventMapper.toRespShort(event);
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
            eventRespShort.setComments(giveComments(eventRespShort.getId()));
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
            participants = requestRepository
                    .countByEventIdAndStatus(eventId, String.valueOf(RequestStatus.CONFIRMED));
            if (!request1.getStatus().equals(String.valueOf(RequestStatus.PENDING))) {
                throw new ValidationException("Неверно указан статус");
            } else if (event.getLimit() < participants) {
                throw new ValidationException("Превышен лимит");
            } else if (requestFor.getStatus().equals("CONFIRMED")) {
                request1.setStatus("CONFIRMED");
            } else if (requestFor.getStatus().equals("REJECTED")) {
                request1.setStatus("REJECTED");
            }
            requestRepository.save(request1);
        }
        if (requestFor.getStatus().equals(String.valueOf(RequestStatus.REJECTED))) {
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
        } else if (!(event.getState().equals(String.valueOf(Status.PENDING)))
                && !(event.getState().equals(String.valueOf(Status.CANCELED)))) {
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

        Event result = updateEvent(event, eventUpdate, category);
        EventDto eventDto = EventMapper.toEventDto(result);
        eventDto.setComments(giveComments(eventDto.getId()));
        return eventDto;
    }

    public List<RequestDto> get(Long userId, Long eventId) {
        try {
            return requestRepository.findByEventId(eventId).stream()
                    .map(request -> RequestMapper.toRequestDto(request)).toList();
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }

    //PublicService
    public List<EventRespShort> search(String text, List<Integer> categories, Boolean paid,
                                       LocalDateTime start, LocalDateTime end,
                                       Boolean available, String sort, int from, int size) {
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

        List<EventRespShort> events;
        Pageable pageable = PageRequest.of(from / size, size);
        if (start != null && end != null) {
            events = eventRepository.searchEvents(text, categories, paid,
                            start, end, available, pageable).stream()
                    .map(event -> EventMapper.toRespShort(event)).toList();
        } else {
            events = eventRepository.findAllByCategories(categories, pageable).stream()
                    .map(event -> EventMapper.toRespShort(event)).toList();
        }
        List<Long> ids = events.stream().map(eventRespShort -> eventRespShort.getId()).toList();
        Map<Integer, Integer> confirmedRequests = requestRepository
                .countByEventIdInAndStatusGroupByEvent(ids, String.valueOf(RequestStatus.CONFIRMED)).stream()
                .collect(Collectors.toMap(EventIdByRequestsCount::getEvent, EventIdByRequestsCount::getCount));
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
            events.get(i).setComments(giveComments(events.get(i).getId()));
        }

        return events;
    }

    public EventRespShort get(Long eventId, String path) {
        try {
            Event event = eventRepository.findByIdAndState(eventId, String.valueOf(Status.PUBLISHED));
            Long requestConfirm =
                    requestRepository.countByEventIdAndStatus(eventId, String.valueOf(RequestStatus.CONFIRMED));
            EventRespShort full = EventMapper.toRespShort(event);
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

            full.setComments(giveComments(full.getId()));
            return full;
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }

    private List<CommentDto> giveComments(Long eventId) {
        return commentRepository.findAllByEventId(eventId).stream()
                .map(comment -> CommentMapper.toCommentDto(comment)).toList();
    }

    public static Event updateEvent(Event event, EventUpdate eventUpdated, Category category) {

        if (eventUpdated.getId() != null) {
            event.setId(eventUpdated.getId());
        }

        if (eventUpdated.getAnnotation() != null) {
            event.setAnnotation(eventUpdated.getAnnotation());
        }

        if (eventUpdated.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventUpdated.getDescription() != null) {
            event.setDescription(eventUpdated.getDescription());
        }

        if (eventUpdated.getLocation() != null) {
            event.setLocation(eventUpdated.getLocation());
        }

        if (eventUpdated.getPaid() != null) {
            event.setPaid(eventUpdated.getPaid());
        }

        if (eventUpdated.getParticipantLimit() != 0) {
            event.setLimit(eventUpdated.getParticipantLimit());
        }

        if (eventUpdated.getRequestModeration() != null) {
            event.setModeration(eventUpdated.getRequestModeration());
        }

        if (eventUpdated.getTitle() != null) {
            event.setTitle(eventUpdated.getTitle());
        }

        if (eventUpdated.getCreatedOn() != null) {
            event.setCreated(eventUpdated.getCreatedOn());
        }

        if (eventUpdated.getStateAction() != null) {
            if (eventUpdated.getStateAction().equals(String.valueOf(StateAction.PUBLISH_EVENT))) {
                event.setState(String.valueOf(Status.PUBLISHED));
                event.setPublishDate(LocalDateTime.now());
            }

            if ((eventUpdated.getStateAction().equals(String.valueOf(StateAction.REJECT_EVENT)))
                    || (eventUpdated.getStateAction().equals(String.valueOf(StateAction.CANCEL_REVIEW)))) {
                event.setState(String.valueOf(Status.CANCELED));
            }

            if (eventUpdated.getStateAction().equals(String.valueOf(StateAction.SEND_TO_REVIEW))) {
                event.setState(String.valueOf(Status.PENDING));
            }

        }
        return event;
    }
}
