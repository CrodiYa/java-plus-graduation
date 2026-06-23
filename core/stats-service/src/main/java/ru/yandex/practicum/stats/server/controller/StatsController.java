package ru.yandex.practicum.stats.server.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.stats.server.service.StatsService;
import ru.yandex.practicum.interaction.stats.controller.StatsClient;
import ru.yandex.practicum.interaction.stats.dto.EndpointHitDto;
import ru.yandex.practicum.interaction.stats.dto.StatsRequest;
import ru.yandex.practicum.interaction.stats.dto.ViewStatsDto;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController implements StatsClient {
    private final StatsService statsService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto hit(EndpointHitDto endpointHitDto) throws FeignException {
        return statsService.saveHit(endpointHitDto);
    }

    @Override
    public List<ViewStatsDto> getStats(Instant start,
                                       Instant end,
                                       List<String> uris,
                                       boolean unique) throws FeignException {
        return statsService.getStats(start, end, uris, unique);
    }

    @Override
    public List<ViewStatsDto> getStats(StatsRequest statsRequest) {
        return statsService.getStats(Instant.parse(statsRequest.getStart()),
                Instant.parse(statsRequest.getEnd()),
                statsRequest.getUris(),
                statsRequest.getUnique());
    }
}
