package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.data.domain.PageRollRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findBy(
            @RequestParam(required = false) Long[] users, // список id пользователей, чьи события нужно найти
            @RequestParam(required = false) EventState[] states, // список состояний в которых находятся искомые события
            @RequestParam(required = false) Long[] categories, // список id категорий в которых будет вестись поиск
            // дата и время не раньше которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            // дата и время не позже которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("==> findBy event: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}," +
                " from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        AdminSearchParams params = AdminSearchParams.builder()
                .users(users).states(states).categories(categories)
                .rangeStart(rangeStart).rangeEnd(rangeEnd).build();
        List<EventFullDto> events = eventService.findBy(params, PageRollRequest.of(from, size));
        log.info("<== findBy event: count = {}", events.size());
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long eventId, @RequestBody @Valid UpdateEventAdminRequest newEvent) {
        log.info("==> update admin event: eventId = {}, newCategory = {}", eventId, newEvent);
        newEvent.setId(eventId);
        EventFullDto event = eventService.update(newEvent);
        log.info("<== update admin event: {}", event);
        return event;
    }
}
