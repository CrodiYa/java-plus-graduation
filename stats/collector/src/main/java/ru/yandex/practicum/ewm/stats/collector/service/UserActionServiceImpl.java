package ru.yandex.practicum.ewm.stats.collector.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.collector.kafka.KafkaClient;
import ru.yandex.practicum.ewm.stats.collector.mapper.ActionMapper;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {

    private final KafkaClient client;
    private final ActionMapper actionMapper;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {

        Instant instant = Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                request.getTimestamp().getNanos());

        UserActionAvro actionAvro = UserActionAvro.newBuilder()
                .setUserId(request.getUserId())
                .setEventId(request.getEventId())
                .setTimestamp(instant)
                .setActionType(actionMapper.toAvro(request.getActionType()))
                .build();

        client.send(actionAvro);

    }
}
