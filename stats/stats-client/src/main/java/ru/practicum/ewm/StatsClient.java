package ru.practicum.ewm;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void hit(EndpointHitDto endpointHit);

    List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
