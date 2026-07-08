package ru.yandex.practicum.ewn.stats.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.util.ActionWeight;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EventSimilarityServiceImpl implements EventSimilarityService {

    /**
     * Map storing maximum weights for each user per event.
     * Key: eventId, Value: Map of userId -> max weight
     */
    private final Map<Long, Map<Long, Double>> eventUserMaxWeights = new ConcurrentHashMap<>();

    /**
     * Map storing total weight sums for each event.
     * Key: eventId, Value: sum of all user weights
     */
    private final Map<Long, Double> eventWeightSums = new ConcurrentHashMap<>();

    /**
     * Map storing minimum weight sums for each pair of events.
     * Key: smaller eventId, Value: Map of larger eventId -> sum of minimum weights
     */
    private final Map<Long, Map<Long, Double>> minWeightsSums = new ConcurrentHashMap<>();

    @Override
    public List<EventSimilarityAvro> processUserAction(UserActionAvro action) {
        long eventId = action.getEventId();
        long userId = action.getUserId();

        double newWeight = ActionWeight.getWeight(action.getActionType());
        double oldWeight = getCurrentWeight(eventId, userId);

        if (newWeight <= oldWeight) {
            return Collections.emptyList();
        }

        updateData(eventId, userId, newWeight, oldWeight);

        return calculateSimilarities(eventId, userId, oldWeight, newWeight, action.getTimestamp());
    }

    /**
     * Updates the weight data for an event-user pair and the total weight sum for the event.
     *
     * @param eventId   the event id
     * @param userId    the user id
     * @param newWeight the new weight value
     * @param oldWeight the old weight value
     */
    private void updateData(long eventId, long userId, double newWeight, double oldWeight) {
        eventUserMaxWeights.computeIfAbsent(eventId, k -> new HashMap<>())
                .put(userId, newWeight);

        double difference = newWeight - oldWeight;
        double newSum = eventWeightSums.getOrDefault(eventId, 0.0) + difference;
        eventWeightSums.put(eventId, newSum);
    }

    /**
     * Calculates similarities between the updated event and all other events.
     * Only processes events that share users with the updated event.
     *
     * @param eventId   the event being updated
     * @param userId    the user who performed the action
     * @param oldWeight the previous weight for this user-event pair
     * @param newWeight the new weight for this user-event pair
     * @param timestamp the timestamp of the action
     * @return list of updated event similarities
     */
    private List<EventSimilarityAvro> calculateSimilarities(long eventId, long userId,
                                                            double oldWeight, double newWeight,
                                                            Instant timestamp) {

        List<EventSimilarityAvro> results = new ArrayList<>();

        for (long otherEventId : eventUserMaxWeights.keySet()) {
            if (otherEventId == eventId) continue;

            Map<Long, Double> otherWeights = eventUserMaxWeights.get(otherEventId);
            Double otherWeight = (otherWeights != null) ? otherWeights.get(userId) : null;

            if (otherWeight == null) continue;

            double similarity = updateAndCalculate(eventId, otherEventId, oldWeight, newWeight, otherWeight);

            if (similarity > 0) {
                results.add(assemblyEventSimilarity(eventId, otherEventId, similarity, timestamp));
            }
        }

        return results;
    }

    /**
     * Updates the minimum weight sum for a pair of events and calculates their similarity.
     *
     * @param eventId1    first event id
     * @param eventId2    second event id
     * @param oldWeight   previous weight for the first event
     * @param newWeight   new weight for the first event
     * @param otherWeight current weight for the second event
     * @return similarity score between the two events
     */
    private double updateAndCalculate(long eventId1, long eventId2,
                                      double oldWeight, double newWeight, double otherWeight) {

        long first = Math.min(eventId1, eventId2);
        long second = Math.max(eventId1, eventId2);

        Map<Long, Double> pairSums = minWeightsSums.computeIfAbsent(first, k -> new HashMap<>());
        double currentMin = pairSums.getOrDefault(second, 0.0);

        double oldMin = Math.min(oldWeight, otherWeight);
        double newMin = Math.min(newWeight, otherWeight);
        double diff = newMin - oldMin;

        pairSums.put(second, currentMin + diff);

        Double sum1 = eventWeightSums.get(first);
        Double sum2 = eventWeightSums.get(second);

        if (sum1 == null || sum2 == null || sum1 <= 0 || sum2 <= 0) {
            return 0.0;
        }

        double sMin = pairSums.get(second);
        return sMin / Math.sqrt(sum1 * sum2);
    }

    /**
     * Retrieves the current weight for a specific user-event pair.
     *
     * @param eventId the event id
     * @param userId  the user id
     * @return the current weight, or 0.0 if not found
     */
    private double getCurrentWeight(long eventId, long userId) {
        Map<Long, Double> weights = eventUserMaxWeights.get(eventId);
        return (weights != null) ? weights.getOrDefault(userId, 0.0) : 0.0;
    }

    /**
     * Assembles an EventSimilarityAvro object with normalized event ids.
     *
     * @param eventId1   first event id
     * @param eventId2   second event id
     * @param similarity the similarity score
     * @param timestamp  the timestamp of the calculation
     * @return EventSimilarityAvro object
     */
    private EventSimilarityAvro assemblyEventSimilarity(long eventId1, long eventId2,
                                                        double similarity, Instant timestamp) {
        long first = Math.min(eventId1, eventId2);
        long second = Math.max(eventId1, eventId2);

        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(similarity)
                .setTimestamp(timestamp)
                .build();
    }
}
