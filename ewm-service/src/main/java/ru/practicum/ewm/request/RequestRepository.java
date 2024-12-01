package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.CountByEvent;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(long requesterId);

    List<Request> findByIdIn(List<Long> ids);

    List<Request> findByEventId(long eventId);

    @Query("SELECT new ru.practicum.ewm.event.CountByEvent(r.event.id, COUNT(r.id))" +
            " FROM Request r" +
            " WHERE r.status = ?2 AND r.event.id IN (?1)" +
            " GROUP BY r.event.id")
    List<CountByEvent> countByStatusRequests(List<Long> eventIds, RequestStatus status);

    Optional<Request> findByIdAndRequesterId(long id, long requesterId);

    long countByEventIdAndStatusIs(long eventId, RequestStatus status);
}
