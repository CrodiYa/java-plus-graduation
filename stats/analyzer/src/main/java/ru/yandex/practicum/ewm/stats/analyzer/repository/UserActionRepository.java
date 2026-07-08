package ru.yandex.practicum.ewm.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.stats.analyzer.model.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    List<UserAction> findByUserIdOrderByTimestampDesc(Long userId);

    List<UserAction> findAllByEventId(Long eventId);
}
