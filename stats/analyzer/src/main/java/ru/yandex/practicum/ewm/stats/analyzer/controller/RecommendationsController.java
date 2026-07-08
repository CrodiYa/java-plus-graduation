package ru.yandex.practicum.ewm.stats.analyzer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.ewm.stats.analyzer.service.RecommendationService;
import ru.yandex.practicum.grpc.stats.action.*;
import ru.yandex.practicum.grpc.stats.service.dashboard.RecommendationsControllerGrpc;

import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    public Stream<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto requestProto) {
        return recommendationService.getRecommendationsForUser(requestProto);
    }

    public Stream<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto similarEventsRequestProto) {
        return recommendationService.getSimilarEvents(similarEventsRequestProto);
    }

    public Stream<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto interactionsCountRequestProto) {
        return recommendationService.getInteractionsCount(interactionsCountRequestProto);
    }
}
