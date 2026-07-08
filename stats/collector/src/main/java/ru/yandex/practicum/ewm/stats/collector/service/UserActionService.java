package ru.yandex.practicum.ewm.stats.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

public interface UserActionService {

    /**
     * Collects and processes a user action from the gRPC request.
     * Saves the user action and triggers similarity calculations.
     *
     * @param request          the user action proto containing event id, user id, action type, and timestamp
     * @param responseObserver the response observer for returning an empty response
     */
    void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver);
}
