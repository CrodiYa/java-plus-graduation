package ru.yandex.practicum.ewm.stats.analyzer.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.analyzer.model.EventSimilarity;

import java.util.Collection;
import java.util.List;

public interface EventSimilarityService {

    /**
     * Saves or updates event similarity records based on the provided list.
     *
     * @param eventSimilarityAvroList list of event similarity Avro objects to update
     */
    void saveSimilarities(List<EventSimilarityAvro> eventSimilarityAvroList);

    /**
     * Retrieves all event similarity records for the specified event ids.
     *
     * @param ids collection of event ids to filter by
     * @return list of event similarity records
     */
    List<EventSimilarity> findByEventIdIn(Collection<Long> ids);

    /**
     * Retrieves the top similar events for a specific event, ordered by score descending.
     *
     * @param eventId id of the event to find similarities for
     * @param limit   maximum number of results to return
     * @return list of event similarity records ordered by score descending
     */
    List<EventSimilarity> findByEventIdOrderByScore(Long eventId, Integer limit);
}
