package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;
import ru.practicum.ewm.validation.constraints.StartBeforeEndParameters;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void hit(@RequestBody @Valid EndpointHitDto endpointHit) {
        log.info("==> hit endpoint: {}", endpointHit);
        statsService.hit(endpointHit);
        log.info("<== hit endpoint");
    }

    @GetMapping("/stats")
    @StartBeforeEndParameters
    public List<ViewStatsDto> stats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("==> stats: start = {}, end = {}, uris = {}, unique = {}, ", start, end, uris, unique);
        List<ViewStatsDto> viewStats = statsService.stats(start, end, uris, unique);
        log.info("<== stats: count = {}", viewStats.size());
        return viewStats;
    }
}
