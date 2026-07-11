package ru.yandex.practicum.ewm.stats.analyzer.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.analyzer.model.UserAction;

import java.util.List;

public interface UserActionService {
    /**
     * Saves or updates a list of user actions.
     *
     * @param userActionAvroList list of user action Avro objects to save
     */
    void saveUserAction(List<UserActionAvro> userActionAvroList);

    /**
     * Retrieves all user actions for a specific user, ordered by timestamp.
     *
     * @param userId id of the user whose actions to retrieve
     * @return list of user actions ordered by timestamp
     */
    List<UserAction> findByUserIdOrderByTimestamp(Long userId);

    /**
     * Calculates the maximum rating per user for a specific event.
     * Aggregates the highest weight action performed by each user on the event.
     *
     * @param eventId id of the event to calculate ratings for
     * @return the sum of maximum user ratings for the event
     */
    Double calculateMaxRatingPerUserByEventId(Long eventId);
}
