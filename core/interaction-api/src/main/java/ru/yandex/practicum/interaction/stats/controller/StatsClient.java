package ru.yandex.practicum.interaction.stats.controller;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.stats.dto.EndpointHitDto;
import ru.yandex.practicum.interaction.stats.dto.StatsRequest;
import ru.yandex.practicum.interaction.stats.dto.ViewStatsDto;

import java.time.Instant;
import java.util.List;

@FeignClient(name = "stats")
public interface StatsClient {

    @PostMapping("/hit")
    EndpointHitDto hit(@RequestBody @Valid EndpointHitDto endpointHitDto);

    @GetMapping("/stats")
    List<ViewStatsDto> getStats(@RequestParam Instant start,
                                @RequestParam Instant end,
                                @RequestParam(required = false) List<String> uris,
                                @RequestParam(defaultValue = "false") boolean unique);
    @PostMapping("/stats")
    List<ViewStatsDto> getStats(@Valid StatsRequest statsRequest);
}

