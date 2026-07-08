package ru.yandex.practicum.ewn.stats.aggregator.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;

public interface EventSimilarityService {

    /**
     * Processes a user action and updates event similarities.
     * If the new action weight is greater than the previous weight for the user-event pair,
     * recalculates similarities with all other events.
     *
     * @param action the user action to process
     * @return list of updated event similarities
     */
    List<EventSimilarityAvro> processUserAction(UserActionAvro action);
}
