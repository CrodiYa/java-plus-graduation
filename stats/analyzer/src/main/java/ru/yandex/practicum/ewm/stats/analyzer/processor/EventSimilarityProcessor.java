package ru.yandex.practicum.ewm.stats.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.ewm.stats.analyzer.kafka.KafkaSimilarityClient;
import ru.yandex.practicum.ewm.stats.analyzer.service.EventSimilarityService;

import java.util.List;

@Slf4j
@Component
public class EventSimilarityProcessor implements Runnable {

    private final KafkaSimilarityClient kafkaClient;
    private final EventSimilarityService similarityService;

    @Autowired
    public EventSimilarityProcessor(KafkaSimilarityClient kafkaClient, EventSimilarityService similarityService) {
        this.kafkaClient = kafkaClient;
        this.similarityService = similarityService;

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaClient::wakeup));
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<EventSimilarityAvro> eventSimilarities = kafkaClient.pollMessages();
                if (!eventSimilarities.isEmpty()) {
                    similarityService.updateSimilarities(eventSimilarities);
                }
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error during processing similarities", e);
        } finally {
            kafkaClient.stop();
        }
    }
}
