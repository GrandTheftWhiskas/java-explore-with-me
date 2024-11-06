package ru.practicum.stat_svc.mapper;

import ru.practicum.StatsDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.stat_svc.model.Stats;

public class StatMapper {
    public static StatsDto toStatDto(Stats stats) {
        try {
            StatsDto statsDto = new StatsDto();
            statsDto.setApp(stats.getApp());
            statsDto.setUri(stats.getUri());
            statsDto.setHits(stats.getHits());
            return statsDto;
        } catch (NullPointerException e) {
            throw new NotFoundException("Событие не найдено");
        }
    }
}
