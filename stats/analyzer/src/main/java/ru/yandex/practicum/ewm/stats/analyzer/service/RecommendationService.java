package ru.yandex.practicum.ewm.stats.analyzer.service;

import ru.yandex.practicum.grpc.stats.action.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.action.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.action.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.action.UserPredictionsRequestProto;

import java.util.stream.Stream;

public interface RecommendationService {

    /**
     * Generates personalized event recommendations for a user based on their action history.
     *
     * @param requestProto request containing user id and optional parameters
     * @return stream of recommended event protos
     */
    Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto requestProto);

    /**
     * Finds events similar to a given event based on user interaction patterns.
     *
     * @param similarEventsRequestProto request containing event id and optional parameters
     * @return stream of similar event protos
     */
    Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto similarEventsRequestProto);

    /**
     * Retrieves events with the highest interaction counts.
     *
     * @param interactionsCountRequestProto request containing filters and pagination parameters
     * @return stream of recommended event protos based on popularity
     */
    Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto interactionsCountRequestProto);
}
