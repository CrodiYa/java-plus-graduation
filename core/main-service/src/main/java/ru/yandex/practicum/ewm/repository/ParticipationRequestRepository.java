package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.model.participation.ParticipationRequest;
import ru.yandex.practicum.ewm.model.participation.ParticipationStatus;
import ru.yandex.practicum.ewm.service.event.EventRequestCount;

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
            WHERE pr.event.id = :eventId
            AND pr.status=:status
            """)
    int rejectPendingRequests(Long eventId, ParticipationStatus status);

    int countByEventIdAndStatus(Long eventId, ParticipationStatus status);

    @Query("""
            SELECT r.event.id as eventId,
            COUNT(r) as count
            FROM ParticipationRequest r
            WHERE r.event.id IN :eventIds
            AND r.status = :status
            GROUP BY r.event.id
            """)
    List<EventRequestCount> countConfirmedRequestsByEventIds(List<Long> eventIds, ParticipationStatus status);
}
