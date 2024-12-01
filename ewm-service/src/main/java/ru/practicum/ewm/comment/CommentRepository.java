package ru.practicum.ewm.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.CountByEvent;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Slice<Comment> findByEventId(long eventId, Pageable pageable);

    long countByEventId(long eventId);

    @Query("SELECT new ru.practicum.ewm.event.CountByEvent(c.event.id, COUNT(c.id))" +
            " FROM Comment c" +
            " WHERE c.event.id IN (?1)" +
            " GROUP BY c.event.id")
    List<CountByEvent> countByEvents(List<Long> eventIds);
}
