package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto add(NewCommentDto newComment) {
        Event event = eventRepository.findById(newComment.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id = " + newComment.getEventId() + " не найдено"));
        User author = userRepository.findById(newComment.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newComment.getAuthorId() + " не найден"));

        // проверяем необходимые условия
        if (event.getState() != EventState.PUBLISHED)
            throw new DataIntegrityViolationException("Нельзя комментировать неопубликованные события");

        Comment c = commentMapper.map(newComment, event, author);
        c.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(c);
        return commentMapper.map(comment);
    }

    @Override
    public CommentDto update(NewCommentDto newComment) {
        Comment oldComment = commentRepository.findById(newComment.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + newComment.getId() + " не найден"));
        Event event = eventRepository.findById(newComment.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id = " + newComment.getEventId() + " не найдено"));
        User author = userRepository.findById(newComment.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newComment.getAuthorId() + " не найден"));

        // проверяем необходимые условия
        if (oldComment.getEvent().getId() != newComment.getEventId())
            throw new DataIntegrityViolationException("Исходное событие не совпадает с событием в новом комментарии");
        if (event.getState() != EventState.PUBLISHED)
            throw new DataIntegrityViolationException("Нельзя комментировать неопубликованные события");
        if (oldComment.getAuthor().getId() != newComment.getAuthorId())
            throw new DataIntegrityViolationException("Нельзя изменять чужие комментарии");

        // если комментарий найден и все условия соблюдены, обновляем его содержимое
        if (StringUtils.isNoneBlank(newComment.getText()))
            oldComment.setText(newComment.getText());
        oldComment.setCreated(LocalDateTime.now());

        Comment comment = commentRepository.save(oldComment);
        return commentMapper.map(comment);
    }

    @Override
    public void delete(long id, long eventId, long authorId) {
        Comment oldComment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + id + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + authorId + " не найден"));

        // проверяем необходимые условия
        if (oldComment.getEvent().getId() != eventId)
            throw new DataIntegrityViolationException("Исходное событие не совпадает с событием в удаляемом комментарии");
        if (event.getState() != EventState.PUBLISHED)
            throw new DataIntegrityViolationException("Нельзя удалять комментарии в неопубликованных событиях");
        if (oldComment.getAuthor().getId() != authorId)
            throw new DataIntegrityViolationException("Нельзя удалять чужие комментарии");

        commentRepository.delete(oldComment);
    }

    @Override
    public List<CommentDto> findByEventId(long eventId, Pageable pageable) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));

        Slice<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return commentMapper.map(comments.getContent());
    }
}
