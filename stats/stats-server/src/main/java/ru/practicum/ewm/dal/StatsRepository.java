package ru.practicum.ewm.dal;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    Long hit(EndpointHitDto endpointHit);

    List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
