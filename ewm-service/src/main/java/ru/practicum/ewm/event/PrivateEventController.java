package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findByUserId(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("==> findByUserId event: userId = {}, from = {}, size = {}", userId, from, size);
        List<EventShortDto> events = eventService.findByInitiatorId(userId, PageRollRequest.of(from, size));
        log.info("<== findByUserId event: count = {}", events.size());
        return events;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId, @RequestBody @Valid NewEventDto newEvent) {
        log.info("==> create event: userId = {}, newEvent = {}", userId, newEvent);
        EventFullDto event = eventService.add(userId, newEvent);
        log.info("<== create event: {}", event);
        return event;
    }

    @GetMapping("/{eventId}")
    public EventFullDto findByIdForInitiator(@PathVariable long userId, @PathVariable long eventId) {
        log.info("==> findByIdForInitiator event: userId = {}, eventId = {}", userId, eventId);
        EventFullDto event = eventService.findByIdForInitiator(eventId, userId);
        log.info("<== findByIdForInitiator event: {}", event);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId, @PathVariable long eventId,
                               @RequestBody @Valid UpdateEventUserRequest newEvent) {
        log.info("==> update event: userId = {}, eventId = {}, newCategory = {}", userId, eventId, newEvent);
        newEvent.setId(eventId);
        EventFullDto event = eventService.update(userId, newEvent);
        log.info("<== update event: {}", event);
        return event;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findByIdRequests(@PathVariable long userId, @PathVariable long eventId) {
        log.info("==> findByIdRequests event: userId = {}, eventId = {}", userId, eventId);
        List<ParticipationRequestDto> requests = eventService.findByIdRequests(userId, eventId);
        log.info("<== findByIdRequests event: count = {}", requests.size());
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult update(@PathVariable long userId, @PathVariable long eventId,
                                                 @RequestBody @Valid EventRequestStatusUpdateRequest newStatusRequest) {
        log.info("==> update event: userId = {}, eventId = {}, newStatusRequest = {}", userId, eventId, newStatusRequest);
        EventRequestStatusUpdateResult statusUpdateResult = eventService.update(userId, eventId, newStatusRequest);
        log.info("<== update event: {}", statusUpdateResult);
        return statusUpdateResult;
    }
}
