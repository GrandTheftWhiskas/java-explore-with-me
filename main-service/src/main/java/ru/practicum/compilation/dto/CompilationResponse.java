package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventRespShort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationResponse {

    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventRespShort> events;

}
