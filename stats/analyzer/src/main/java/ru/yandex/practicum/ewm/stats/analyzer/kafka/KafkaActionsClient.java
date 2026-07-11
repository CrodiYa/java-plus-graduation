package ru.yandex.practicum.ewm.stats.analyzer.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewm.stats.analyzer.kafka.config.KafkaProperties;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaActionsClient implements KafkaClient<UserActionAvro> {

    private static final long OFFSET_INCREMENT = 1L;

    private Duration consumeAttemptTimeout;
    private final KafkaProperties kafkaProperties;

    private Consumer<Void, SpecificRecordBase> consumer;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();

    @Override
    @PostConstruct
    public void init() {
        this.consumeAttemptTimeout = Duration.ofMillis(kafkaProperties.getConsumeAttemptTimeout());

        this.consumer = new KafkaConsumer<>(getConfig());
        this.consumer.subscribe(Collections.singletonList(kafkaProperties.getTopic().getActions()));
    }

    @Override
    public List<UserActionAvro> pollMessages() {
        List<UserActionAvro> messages = new ArrayList<>();

        try {
            ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(consumeAttemptTimeout);

            if (records.isEmpty()) return Collections.emptyList();

            for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                if (record.value() instanceof UserActionAvro) {
                    messages.add((UserActionAvro) record.value());
                }
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + OFFSET_INCREMENT));
            }

            if (!records.isEmpty()) {
                consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Error during committing offsets: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException e) {
            log.debug("Consumer wakeup called");
        } catch (Exception e) {
            log.error("Error polling messages from Kafka", e);
        }

        return messages;
    }

    @Override
    public void wakeup() {
        this.consumer.wakeup();
    }

    @Override
    public Properties getConfig() {
        Properties config = new Properties();
        Map<String, String> props = kafkaProperties.getActionConsumer().getProperties();
        props.forEach(config::setProperty);
        return config;
    }

    @Override
    @PreDestroy
    public void stop() {
        if (consumer != null) {
            consumer.close();
            log.info("Kafka action consumer closed");
        }

    }
}
