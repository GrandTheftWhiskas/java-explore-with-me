package ru.practicum.stat_svc.dto;

import lombok.Data;

@Data
public class EventDto {
    private String app;
    private String uri;
    private int hits;
}
