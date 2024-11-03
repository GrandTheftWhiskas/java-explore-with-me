package ru.practicum.stat_svc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class EventDto {
    private String app;
    private String uri;
    private int hits;
}
