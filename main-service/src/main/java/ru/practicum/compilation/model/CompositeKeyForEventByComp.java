package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "events_by_compilations")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CompositeKeyForEventByComp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "compilation_id")
    private final Long compilationId;
    @Column(name = "event_id")
    private final Long eventId;

}
