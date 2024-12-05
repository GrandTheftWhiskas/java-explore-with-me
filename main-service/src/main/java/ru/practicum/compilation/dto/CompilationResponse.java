package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.event.dto.EventRespShort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationResponse {

    private Long id;
    @Length(max = 50)
    private String title;
    private Boolean pinned;
    private List<EventRespShort> events;

}
