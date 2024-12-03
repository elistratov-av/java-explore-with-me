package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.data.domain.PageRollRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;
    protected final StatsClient statsClient;

    private void hit(HttpServletRequest request) {
        if (request == null) return;

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        LocalDateTime cur = LocalDateTime.now();

        EndpointHitDto endpointHit = new EndpointHitDto();
        endpointHit.setApp("ewm-main-service");
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(cur);
        statsClient.hit(endpointHit);
    }

    private void hitAndMerge(EventViews event, HttpServletRequest request) {
        if (request == null) return;

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        LocalDateTime cur = LocalDateTime.now();

        EndpointHitDto endpointHit = new EndpointHitDto();
        endpointHit.setApp("ewm-main-service");
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(cur);
        statsClient.hit(endpointHit);

        List<ViewStatsDto> stats = statsClient.stats(cur.minusMinutes(1), cur.plusMinutes(1),
                new String[]{uri}, true);
        if (stats != null && !stats.isEmpty())
            event.setViews(stats.getFirst().getHits());
    }

    private void hitAndMerge(List<? extends EventViews> events, HttpServletRequest request) {
        if (request == null) return;

        String ip = request.getRemoteAddr();
        LocalDateTime cur = LocalDateTime.now();

        EndpointHitDto endpointHit = new EndpointHitDto();
        endpointHit.setApp("ewm-main-service");
        endpointHit.setIp(ip);
        endpointHit.setTimestamp(cur);

        Map<String, EventViews> eventMap = new HashMap<>();
        for (EventViews e : events) {
            String uri = "/events/" + e.getId();
            eventMap.put(uri, e);
            endpointHit.setUri(uri);
            statsClient.hit(endpointHit);
        }

        Set<String> uris = eventMap.keySet();
        List<ViewStatsDto> stats = statsClient.stats(cur.minusMinutes(1), cur.plusMinutes(1),
                uris.toArray(new String[0]), true);
        if (stats != null && !stats.isEmpty()) {
            for (ViewStatsDto s : stats) {
                String uri = s.getUri();
                long hits = s.getHits();
                EventViews e = eventMap.get(uri);
                if (e != null)
                    e.setViews(hits);
            }
        }
    }

    @GetMapping
    public List<EventShortDto> findBy(
            @RequestParam(required = false) String text, // текст для поиска в содержимом аннотации и подробном описании события
            @RequestParam(required = false) Long[] categories, // список идентификаторов категорий в которых будет вестись поиск
            @RequestParam(required = false) Boolean paid, // поиск только платных/бесплатных событий
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart, // дата и время не раньше которых должно произойти событие
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd, // дата и время не позже которых должно произойти событие
            @RequestParam(defaultValue = "false") Boolean onlyAvailable, // только события у которых не исчерпан лимит запросов на участие
            @RequestParam(required = false) EventSort sort, // Вариант сортировки: по дате события или по количеству просмотров; Available values : EVENT_DATE, VIEWS
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {
        log.info("==> findBy event: text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        SearchParams params = SearchParams.builder()
                .text(text).categories(categories).paid(paid)
                .rangeStart(rangeStart).rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable).sort(sort).build();
        List<EventShortDto> events = eventService.findPublishedBy(params, PageRollRequest.of(from, size));
        hit(request);
        hitAndMerge(events, request);
        log.info("<== findBy event: count = {}", events.size());
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto findById(@PathVariable long id, HttpServletRequest request) {
        log.info("==> findById event: id = {}", id);
        EventFullDto event = eventService.findPublishedById(id);
        hitAndMerge(event, request);
        log.info("<== findById event: {}", event);
        return event;
    }
}
