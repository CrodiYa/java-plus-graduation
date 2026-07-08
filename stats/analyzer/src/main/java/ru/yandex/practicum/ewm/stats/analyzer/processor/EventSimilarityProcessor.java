package ru.yandex.practicum.ewm.stats.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.analyzer.kafka.KafkaClient;
import ru.yandex.practicum.ewm.stats.analyzer.service.EventSimilarityService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityProcessor implements Runnable {

    private final KafkaClient kafkaClient;
    private final EventSimilarityService similarityService;

    @Override
    public void run() {
        try {
            while (true) {
                List<EventSimilarityAvro> eventSimilarities = kafkaClient.pollEventSimilarity();
                similarityService.updateSimilarities(eventSimilarities);
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error during processing similarities", e);
        } finally {
            kafkaClient.stop();
        }
    }
}
