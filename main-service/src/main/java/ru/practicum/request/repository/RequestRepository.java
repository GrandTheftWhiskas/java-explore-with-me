package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.dto.EventIdByRequestsCount;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request getRequestById(Long id);

    Long countByEventIdAndStatus(long eventId, String status);

    List<Request> findByRequesterId(Long id);

    List<Request> findByEventId(Long eventId);

    @Query(value = "select * " +
          "FROM requests " +
    "WHERE event IN ?1", nativeQuery = true)
    List<Request> findByEventIdIn(List<Long> ids);

    @Query(value = "select count(id), event " +
            "from requests " +
            "where event IN ?1 " +
            "AND status LIKE ?2 " +
            "group by event ", nativeQuery = true)
    List<EventIdByRequestsCount> countByEventIdInAndStatusGroupByEvent(List<Long> eventId, String status);

    @Query(value = "select id " +
            "from requests " +
            "where event IN ?1 " +
            "AND status LIKE ?2 " +
            "group by event ", nativeQuery = true)
    List<EventIdByRequestsCount> findIdByEventIdInAndStatusGroupByEvent(List<Long> eventId, String requestState);

    Request findByEventIdAndStatus(Long eventId, String requestState);

    List<Request> findByIdInAndEventId(List<Long> id, long eventId);
}
