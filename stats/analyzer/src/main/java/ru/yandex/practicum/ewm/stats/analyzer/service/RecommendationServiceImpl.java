package ru.yandex.practicum.ewm.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.stats.analyzer.model.EventSimilarity;
import ru.yandex.practicum.ewm.stats.analyzer.model.UserAction;
import ru.yandex.practicum.ewm.stats.util.ActionWeight;
import ru.yandex.practicum.grpc.stats.action.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.action.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.action.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.action.UserPredictionsRequestProto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final EventSimilarityService eventSimilarityService;
    private final UserActionService userActionService;

    @Override
    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto requestProto) {

        List<UserAction> userActions = userActionService.findByUserIdOrderByTimestamp(requestProto.getUserId());

        if (userActions.isEmpty()) {
            return Stream.empty();
        }

        Set<Long> userEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        Map<Long, Double> eventRatings = userActions.stream()
                .collect(Collectors.toMap(
                        ua -> ua.getEventId(),
                        ua -> ActionWeight.getWeight(ua.getActionType()),
                        Math::max
                ));

        List<EventSimilarity> similarities = eventSimilarityService.findByEventIdIn(userEventIds);

        Map<Long, Double> candidateScores = new HashMap<>();

        for (EventSimilarity s : similarities) {
            Long candidateId = extractCandidate(s, userEventIds);
            if (candidateId == null) continue;

            Long userEventId = getOppositeEventId(s, candidateId);

            Double weight = eventRatings.get(userEventId);
            if (weight != null) {
                double score = weight * s.getScore();
                candidateScores.merge(candidateId, score, Math::max);
            }
        }

        return candidateScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(requestProto.getMaxResults())
                .map(entry -> assembleRecommendedEvent(entry.getKey(), entry.getValue()));

    }

    @Override
    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto requestProto) {
        return eventSimilarityService.findByEventIdOrderByScore(requestProto.getEventId(), requestProto.getMaxResults())
                .stream()
                .map(s -> {
                    Long otherId = getOppositeEventId(s, requestProto.getEventId());
                    return assembleRecommendedEvent(otherId, s.getScore());
                });
    }

    @Override
    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto requestProto) {
        return requestProto.getEventIdList().stream()
                .distinct()
                .map(eventId -> {
                    Double sum = userActionService.calculateMaxRatingPerUserByEventId(eventId);
                    return assembleRecommendedEvent(eventId, sum);
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()));
    }

    /**
     * Assembles a RecommendedEventProto object from event id and score.
     *
     * @param eventId id of the recommended event
     * @param score   similarity or relevance score
     * @return RecommendedEventProto object
     */
    private RecommendedEventProto assembleRecommendedEvent(Long eventId, Double score) {
        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score != null ? score : 0.0)
                .build();
    }

    /**
     * Retrieves the opposite event id from an EventSimilarity record.
     * If the provided id matches eventA, returns eventB, and vice versa.
     *
     * @param s  EventSimilarity record containing a pair of event ids
     * @param id the event id that is known to be part of the pair
     * @return the opposite event id
     */
    private Long getOppositeEventId(EventSimilarity s, Long id) {
        return s.getEventA().equals(id) ? s.getEventB() : s.getEventA();
    }

    /**
     * Extracts a candidate event id from an EventSimilarity record.
     * Returns the event id that is not present in the set of user event ids.
     * Returns null if both or neither event ids are in the set.
     *
     * @param eventSimilarity EventSimilarity record containing a pair of event ids
     * @param userEventIds    set of event ids already interacted with by the user
     * @return the candidate event id if exactly one of the pair is in the set, null otherwise
     */
    private Long extractCandidate(EventSimilarity eventSimilarity, Set<Long> userEventIds) {
        // we can`t trust input data, so we double-check
        if (userEventIds.contains(eventSimilarity.getEventA())) {
            return eventSimilarity.getEventB();
        } else if (userEventIds.contains(eventSimilarity.getEventB())) {
            return eventSimilarity.getEventA();
        }
        return null;
    }
}
