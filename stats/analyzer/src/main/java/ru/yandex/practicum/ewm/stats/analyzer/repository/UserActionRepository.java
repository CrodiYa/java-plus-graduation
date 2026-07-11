package ru.yandex.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.yandex.practicum.ewm.stats.analyzer.model.UserAction;

import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    List<UserAction> findByUserIdOrderByTimestampDesc(Long userId);

    List<UserAction> findAllByEventId(Long eventId);
}
