package ru.yandex.practicum.client;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.stats.action.UserActionControllerGrpc;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

@Slf4j
@Component
public class CollectorClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userActionClient;

    public void sendUserAction(UserActionProto userAction) {
        try {
            userActionClient.collectUserAction(userAction);
        } catch (StatusRuntimeException e) {
            log.error("Error during sending action to Collector: {}", e.getStatus().getDescription());
        }
    }
}
