package ru.yandex.practicum.ewn.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewn.stats.aggregator.kafka.KafkaClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaClient kafkaClient;
    private final EventSimilarityServiceImpl eventSimilarityService;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaClient::wakeup));

        try {
            while (true) {
                List<UserActionAvro> userActions = kafkaClient.pollUserActions();

                if (!userActions.isEmpty()) {
                    for (UserActionAvro userAction : userActions) {
                        List<EventSimilarityAvro> similarities = eventSimilarityService.processUserAction(userAction);

                        for (EventSimilarityAvro similarity : similarities) {
                            kafkaClient.sendToSimilarity(similarity);
                        }
                    }
                }
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error during processing user actions", e);
        } finally {
            kafkaClient.stop();
        }
    }

}