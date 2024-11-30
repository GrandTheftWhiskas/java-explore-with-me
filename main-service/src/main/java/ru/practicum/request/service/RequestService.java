package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestDto add(Long userId, Long eventId) {
        try {
            Event event = eventRepository.getEventById(eventId);
            Request request = new Request();
            request.setRequester(userRepository.getUserById(userId));
            request.setEvent(event);
            request.setStatus("PENDING");
            if (!event.getModeration() || event.getLimit() == 0) {
                request.setStatus("CONFIRMED");
            }

            List<Request> requests = requestRepository.findByEventId(eventId);
            int size = requests.size();
            requests = requests.stream()
                    .filter(request1 -> request1.getRequester().getId().equals(userId)).toList();
            if (!requests.isEmpty()) {
                throw new ConflictException("Данный пользователь ранее уже подавал заявку");
            } else if (event.getInit().getId().equals(userId)) {
                throw new ConflictException("Инициатор события не может подать заявку на участие в нем");
            } else if (!event.getState().equals("PUBLISHED")) {
                throw new ConflictException("Событие не опубликовано");
            } else if (event.getLimit() != 0) {
                if (size >= event.getLimit()) {
                    throw new ConflictException("Превышен лимит участников");
                }
            }

            request.setCreated(LocalDateTime.now());
            return RequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NullPointerException e) {
            throw new NotFoundException("Cущность не найдена");
        }
    }

    public List<RequestDto> getAllOwn(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(request -> RequestMapper.toRequestDto(request)).toList();
    }

    public RequestDto cancel(Long userId, Long requestId) {
        try {
            userRepository.getUserById(userId);
            Request request = requestRepository.getRequestById(requestId);
            request.setStatus("CANCELED");
            return RequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NullPointerException e) {
            throw new NotFoundException("Сущность не найдена");
        }
    }
}

