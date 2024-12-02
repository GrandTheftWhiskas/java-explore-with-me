package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

        Event save(Event event);

        Event getEventById(Long id);

        List<Event> findByIdIn(List<Long> id);

        @Query(value = "SELECT * FROM events AS e " +
                "WHERE e.init = ?1 " +
                "LIMIT ?2", nativeQuery = true)
        List<Event> findByInitId(long userId, int limit);

        List<Event> findByInitIdAndState(long userId, String state, Pageable pageable);

        Event findByIdAndState(long id, String state);

        @Query(value = "SELECT e.* " +
                "FROM events AS e " +
                "WHERE ((e.state IN (?1) OR ?1 IS NULL) " +
                "AND (e.category IN (?2) OR ?2 IS NULL) " +
                "AND (e.init IN (?3) OR ?3 IS NULL) " +
                "AND (e.event_date BETWEEN ?4 AND ?5)) " +
                "LIMIT ?6", nativeQuery = true)
        List<Event> findByConditionals(List<String> state, List<Integer> category, List<Long> initiator,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, int limit);

        @Query(value = "SELECT e.* " +
                "FROM events AS e " +
                "WHERE e.category IN (?1) " +
                "LIMIT ?2", nativeQuery = true)
        List<Event> findAllByCategories(List<Integer> categories, int limit);

        @Query(value = "SELECT * FROM events LIMIT ?1")
        List<Event> findAll(int limit);

        @Query(value = "SELECT * " +
                "FROM events AS e " +
                "WHERE (((e.annotation ILIKE %?1% OR e.description ILIKE %?1%) OR ?1 IS NULL) " +
                "AND (e.category IN (?2) OR ?2 IS NULL) " +
                "AND (e.paid = CAST(?3 AS boolean) OR ?3 IS NULL) " +
                "AND (e.event_date BETWEEN ?4 AND ?5 ) " +
                "AND (CAST(?6 AS BOOLEAN) is TRUE " +
                "  OR( " +
                "  select count(id) " +
                "  from requests AS r " +
                "  WHERE r.event = e.id) < limited) " +
                "AND state = 'PUBLISHED') " +
                "LIMIT ?7", nativeQuery = true)
        List<Event> searchEvents(String text, List<Integer> category, Boolean paid, LocalDateTime rangStart,
                                 LocalDateTime rangeEnd, boolean isAvailable, int from);
}
//
