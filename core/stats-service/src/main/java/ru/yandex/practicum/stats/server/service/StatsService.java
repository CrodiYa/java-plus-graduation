package ru.yandex.practicum.stats.server.service;

import ru.yandex.practicum.interaction.stats.dto.EndpointHitDto;
import ru.yandex.practicum.interaction.stats.dto.ViewStatsDto;

import java.time.Instant;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> getStats(Instant start, Instant end,
                                List<String> uris, boolean unique);

    EndpointHitDto saveHit(EndpointHitDto dto);
}
