package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.dal.StatsRepository;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void hit(EndpointHitDto endpointHit) {
        statsRepository.hit(endpointHit);
    }

    @Override
    public List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        return statsRepository.stats(start, end, uris, unique);
    }
}
