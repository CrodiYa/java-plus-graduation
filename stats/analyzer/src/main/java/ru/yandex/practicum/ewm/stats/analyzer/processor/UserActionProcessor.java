package ru.yandex.practicum.ewm.stats.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.analyzer.kafka.KafkaClient;
import ru.yandex.practicum.ewm.stats.analyzer.service.UserActionService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor implements Runnable {

    private final KafkaClient kafkaClient;
    private final UserActionService userActionService;

    @Override
    public void run() {
        try {
            while (true) {
                List<UserActionAvro> userActionAvroList = kafkaClient.pollUserActions();
                userActionService.saveUserAction(userActionAvroList);
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error during processing similarities", e);
        } finally {
            kafkaClient.stop();
        }
    }
}