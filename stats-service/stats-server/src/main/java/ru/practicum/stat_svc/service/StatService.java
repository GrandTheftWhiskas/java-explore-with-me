package ru.practicum.stat_svc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.EventDto;
import ru.practicum.StatsDto;
import ru.practicum.stat_svc.mapper.EventMapper;
import ru.practicum.stat_svc.model.Event;
import ru.practicum.stat_svc.repository.StatRepository;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatService {
    @Autowired
    private final StatRepository statRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventDto post(EventDto eventDto) {
        Event event = new Event();
        event.setApp(eventDto.getApp());
        event.setUri(eventDto.getUri());
        event.setIp(eventDto.getIp());
        event.setPeriod(LocalDateTime.now());
        return EventMapper.toEventDto(statRepository.save(event));
    }

    public EventDto get(Long id) {
        return EventMapper.toEventDto(statRepository.getEventById(id));
    }

    public List<StatsDto> getAll(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime convertStart =
                LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), formatter);
        LocalDateTime convertEnd =
                LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), formatter);

        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return statRepository.getAllUnique(convertStart, convertEnd);
            }
            return statRepository.getAllUniqueWithUris(convertStart, convertEnd, uris);
        }
        if (uris == null || uris.isEmpty()) {
            return statRepository.getAll(convertStart, convertEnd);
        }
        return statRepository.getAllWithUris(convertStart, convertEnd, uris);
    }
}
