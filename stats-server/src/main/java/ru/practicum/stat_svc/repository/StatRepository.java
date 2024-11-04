package ru.practicum.stat_svc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stat_svc.model.Event;

import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Event, Long> {
    Event save(Event event);

    Event getEventById(Long id);

    @Query("select e from Event e " +
            "where e.app = ?1 and e.uri = ?2")
    List<Event> getEvents(String app, String uri);
}
