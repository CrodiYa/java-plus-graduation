package ru.yandex.practicum.ewm.stats.collector.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

@Component
public class ActionMapper {

    public ru.practicum.ewm.stats.avro.ActionTypeAvro toAvro(ru.yandex.practicum.grpc.stats.action.ActionTypeProto type) {
        return switch (type) {
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            default -> null;
        };
    }
}
