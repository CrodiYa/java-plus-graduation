package ru.yandex.practicum.stats.server.service;

import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;
import ru.yandex.practicum.stats.server.exceptions.BadRequestException;

import java.time.Instant;
import java.util.List;

public interface StatsService {

    /**
     * Retrieves statistics for endpoint hits within the specified time range.
     * Optionally filters by URIs and distinguishes unique IP addresses.
     *
     * @param start  start date-time for filtering hits after this point (inclusive)
     * @param end    end date-time for filtering hits before this point (inclusive)
     * @param uris   list of URIs to filter by
     * @param unique if true, counts only unique IP addresses per URI
     * @return list of view statistics DTOs
     * @throws BadRequestException if start is after or equal to end
     */
    List<ViewStatsDto> getStats(Instant start, Instant end,
                                List<String> uris, boolean unique);

    /**
     * Saves a new endpoint hit record.
     *
     * @param dto DTO containing hit details (app, uri, ip, timestamp)
     * @return saved endpoint hit DTO
     */

    EndpointHitDto saveHit(EndpointHitDto dto);
}
