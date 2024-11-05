package ru.practicum.stat_svc.model;

import lombok.Data;

@Data
public class Stats {
    private String app;
    private String uri;
    private int hits;
}
