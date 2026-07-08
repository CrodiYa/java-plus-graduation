package ru.yandex.practicum.ewm.stats.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.analyzer.model.EventSimilarity;

@Component
public class EventSimilarityMapper {

    public EventSimilarity toEventSimilarity(EventSimilarityAvro avro) {
        EventSimilarity eventSimilarity = new EventSimilarity();
        eventSimilarity.setEventA(avro.getEventA());
        eventSimilarity.setEventB(avro.getEventB());
        eventSimilarity.setScore(avro.getScore());
        eventSimilarity.setTimestamp(avro.getTimestamp());
        return eventSimilarity;
    }
}
