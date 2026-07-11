package ru.yandex.practicum.ewm.stats.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.ewm.stats.collector.service.UserActionService;
import ru.yandex.practicum.grpc.stats.action.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionService userActionService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Collecting user action for user {}: action={}", request.getUserId(), request.getActionType());
            userActionService.collectUserAction(request, responseObserver);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error processing request: {}", e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}