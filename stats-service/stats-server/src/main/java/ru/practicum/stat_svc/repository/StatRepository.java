package ru.practicum.stat_svc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.StatsDto;
import ru.practicum.stat_svc.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Event, Long> {
    Event save(Event event);

    Event getEventById(Long id);

    @Query("SELECT new ru.practicum.StatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Event AS e " +
            "WHERE e.period BETWEEN ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<StatsDto> getAll(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.StatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Event AS e " +
            "WHERE e.uri IN ?3 " +
            "AND e.period BETWEEN ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<StatsDto> getAllWithUris(LocalDateTime start, LocalDateTime end, Iterable<String> uris);

    @Query("SELECT new ru.practicum.StatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Event AS e " +
            "WHERE e.period BETWEEN ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<StatsDto> getAllUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.StatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Event AS e " +
            "WHERE e.uri IN ?3 " +
            "AND e.period BETWEEN ?1 AND ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC ")
    List<StatsDto> getAllUniqueWithUris(LocalDateTime start, LocalDateTime end, Iterable<String> uris);
}
