package ru.practicum.ewm.comment;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    CommentDto add(NewCommentDto newComment);

    CommentDto update(NewCommentDto newComment);

    void delete(long id, long eventId, long authorId);

    List<CommentDto> findByEventId(long eventId, Pageable pageable);
}
