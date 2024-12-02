package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
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
                "WHERE e.init = ?1 ", nativeQuery = true)
        Page<Event> findByInitId(long userId, Pageable pageable);

        Page<Event> findByInitIdAndState(long userId, String state, Pageable pageable);

        Event findByIdAndState(long id, String state);

        @Query(value = "SELECT e.* " +
                "FROM events AS e " +
                "WHERE ((e.state IN (?1) OR ?1 IS NULL) " +
                "AND (e.category IN (?2) OR ?2 IS NULL) " +
                "AND (e.init IN (?3) OR ?3 IS NULL) " +
                "AND (e.event_date BETWEEN ?4 AND ?5)) ", nativeQuery = true)
        Page<Event> findByConditionals(List<String> state, List<Integer> category, List<Long> initiator,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

        @Query(value = "SELECT e.* " +
                "FROM events AS e " +
                "WHERE e.category IN (?1) ", nativeQuery = true)
        Page<Event> findAllByCategories(List<Integer> categories, Pageable pageable);

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
                "AND state = 'PUBLISHED')", nativeQuery = true)
        Page<Event> searchEvents(String text, List<Integer> category, Boolean paid, LocalDateTime rangStart,
                                 LocalDateTime rangeEnd, boolean isAvailable, Pageable pageable);
}

