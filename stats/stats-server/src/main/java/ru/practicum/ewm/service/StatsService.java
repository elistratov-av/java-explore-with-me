package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void hit(EndpointHitDto endpointHit);

    List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
