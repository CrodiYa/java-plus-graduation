package ru.yandex.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.dto.ViewStatsDto;
import ru.yandex.practicum.stats.server.model.EndpointHit;

import java.time.Instant;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("""
            SELECT new ru.yandex.practicum.dto.ViewStatsDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip))
            FROM EndpointHit as hit
            WHERE hit.createDate BETWEEN :start AND :end AND hit.uri IN :uris
            GROUP BY hit.app, hit.uri
            ORDER BY COUNT(DISTINCT hit.ip) desc
            """)
    List<ViewStatsDto> getStatsByUriWithUniqueIp(@Param("start") Instant start,
                                                 @Param("end") Instant end,
                                                 @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.yandex.practicum.dto.ViewStatsDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip))
            FROM EndpointHit as hit
            WHERE hit.createDate BETWEEN :start AND :end
            GROUP BY hit.app, hit.uri
            ORDER BY COUNT(DISTINCT hit.ip) desc
            """)
    List<ViewStatsDto> getStatsWithUniqueIp(@Param("start") Instant start,
                                            @Param("end") Instant end);

    @Query("""
            SELECT new ru.yandex.practicum.dto.ViewStatsDto(hit.app, hit.uri, COUNT(hit))
            FROM EndpointHit as hit
            WHERE hit.createDate BETWEEN :start AND :end AND hit.uri IN :uris
            GROUP BY hit.app, hit.uri
            ORDER BY COUNT(hit) desc
            """)
    List<ViewStatsDto> getStatsByUri(@Param("start") Instant start,
                                     @Param("end") Instant end,
                                     @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.yandex.practicum.dto.ViewStatsDto(hit.app, hit.uri, COUNT(hit))
            FROM EndpointHit as hit
            WHERE hit.createDate BETWEEN :start AND :end
            GROUP BY hit.app, hit.uri
            ORDER BY COUNT(hit) desc
            """)
    List<ViewStatsDto> getStats(@Param("start") Instant start,
                                @Param("end") Instant end);
}