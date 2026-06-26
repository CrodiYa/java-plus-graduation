package ru.yandex.practicum.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.participation.EventRequestCount;
import ru.yandex.practicum.interaction.dto.participation.ParticipationStatus;
import ru.yandex.practicum.participation.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    @Modifying
    @Transactional
    @Query("""
            UPDATE ParticipationRequest pr
            SET pr.status = 'REJECTED'
            WHERE pr.eventId = :eventId
            AND pr.status=:status
            """)
    int rejectPendingRequests(Long eventId, ParticipationStatus status);

    int countByEventIdAndStatus(Long eventId, ParticipationStatus status);

    @Query("""
            SELECT new ru.yandex.practicum.interaction.dto.participation.EventRequestCount (r.eventId, COUNT(r) as count)
            FROM ParticipationRequest r
            WHERE r.eventId IN :eventIds
            AND r.status = :status
            GROUP BY eventId
            """)
    List<EventRequestCount> countConfirmedRequestsByEventIds(List<Long> eventIds, ParticipationStatus status);
}
