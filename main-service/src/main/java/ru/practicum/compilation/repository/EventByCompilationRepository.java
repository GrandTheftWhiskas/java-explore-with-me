package ru.practicum.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.compilation.dto.EventByCompId;
import ru.practicum.compilation.model.CompositeKeyForEventByComp;

import java.util.Collection;
import java.util.List;

public interface EventByCompilationRepository extends JpaRepository<CompositeKeyForEventByComp, Long> {

    @Query(value = "SELECT event_id " +
            "FROM events_by_compilations " +
            "WHERE compilation_id = ?1 ",
            nativeQuery = true)
    List<Long> findByCompilationId(Long compilationId);

    @Query(value = "select compilation_id, e.* " +
            "from events_by_compilations AS ebc " +
            "INNER JOIN events AS e on ebc.event_id = e.id " +
            "where compilation_id IN (?1) ",
            nativeQuery = true)
    List<EventByCompId> findEventsByCompIdIn(List<Long> compId);


}
