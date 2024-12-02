package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@ToString
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester")
    private User requester;
    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;
    @Column(name = "status")
    private String status;
    @Column(name = "created")
    private LocalDateTime created;
}
