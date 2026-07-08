package ru.yandex.practicum.ewm.stats.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.analyzer.kafka.KafkaActionsClient;
import ru.yandex.practicum.ewm.stats.analyzer.service.UserActionService;

import java.util.List;

@Slf4j
@Component
public class UserActionProcessor implements Runnable {

    private final KafkaActionsClient kafkaClient;
    private final UserActionService userActionService;

    @Autowired
    public UserActionProcessor(KafkaActionsClient kafkaClient, UserActionService userActionService) {
        this.kafkaClient = kafkaClient;
        this.userActionService = userActionService;

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaClient::wakeup));
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<UserActionAvro> userActionAvroList = kafkaClient.pollMessages();
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