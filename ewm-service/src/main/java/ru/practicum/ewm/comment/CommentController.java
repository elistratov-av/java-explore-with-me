package ru.practicum.ewm.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.data.domain.PageRollRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> findByEventId(
            @PathVariable long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("==> findByEventId comment: eventId = {}, from = {}, size = {}", eventId, from, size);
        List<CommentDto> comments = commentService.findByEventId(eventId, PageRollRequest.of(from, size));
        log.info("<== findByEventId comment: count = {}", comments.size());
        return comments;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto create(@PathVariable long eventId, @RequestParam long userId, @RequestBody @Valid NewCommentDto newComment) {
        log.info("==> create comment: eventId = {}, userId = {}, newComment = {}", eventId, userId, newComment);
        newComment.setEventId(eventId);
        newComment.setAuthorId(userId);
        CommentDto comment = commentService.add(newComment);
        log.info("<== create comment: {}", comment);
        return comment;
    }

    @PatchMapping("/{id}")
    public CommentDto update(@PathVariable long eventId, @PathVariable long id, @RequestParam long userId, @RequestBody @Valid NewCommentDto newComment) {
        log.info("==> update comment: eventId = {}, id = {}, userId = {}", eventId, id, userId);
        newComment.setId(id);
        newComment.setEventId(eventId);
        newComment.setAuthorId(userId);
        CommentDto comment = commentService.update(newComment);
        log.info("<== update comment: {}", comment);
        return comment;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long eventId, @PathVariable long id, @RequestParam long userId) {
        log.info("==> delete comment: eventId = {}, id = {}, userId = {}", eventId, id, userId);
        commentService.delete(id, eventId, userId);
        log.info("<== delete comment");
    }
}
