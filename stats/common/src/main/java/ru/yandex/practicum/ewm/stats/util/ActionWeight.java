package ru.yandex.practicum.ewm.stats.util;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.ewm.stats.enums.ActionType;

public class ActionWeight {

    private static final double VIEW_COEF = 0.4;
    private static final double REGISTER_COEF = 0.8;
    private static final double LIKE_COEF = 1.0;

    /**
     * Returns the weight value for a given action type.
     *
     * @param actionTypeAvro the action type
     * @return weight value: VIEW = 0.4, REGISTER = 0.8, LIKE = 1.0
     */
    public static double getWeight(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case ActionTypeAvro.VIEW -> VIEW_COEF;
            case ActionTypeAvro.REGISTER -> REGISTER_COEF;
            case ActionTypeAvro.LIKE -> LIKE_COEF;
        };
    }

    /**
     * Returns the weight value for a given action type.
     *
     * @param actionType the action type
     * @return weight value: VIEW = 0.4, REGISTER = 0.8, LIKE = 1.0
     */
    public static double getWeight(ActionType actionType) {
        return switch (actionType) {
            case ActionType.VIEW -> VIEW_COEF;
            case ActionType.REGISTER -> REGISTER_COEF;
            case ActionType.LIKE -> LIKE_COEF;
        };
    }
}
