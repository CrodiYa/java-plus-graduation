package ru.yandex.practicum.ewm.stats.analyzer.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class KafkaClient {

    @Autowired
    private KafkaProperties kafkaProperties;

    private Consumer<Void, SpecificRecordBase> actionConsumer;
    private Consumer<Void, SpecificRecordBase> similarityConsumer;

    private Duration consumeAttemptTimeout;

    @PostConstruct
    private void init() {
        String actionsTopic = kafkaProperties.getTopic().getActions();
        String similarityTopic = kafkaProperties.getTopic().getSimilarity();

        this.actionConsumer = initAndGetConsumer(actionsTopic);
        this.similarityConsumer = initAndGetConsumer(similarityTopic);

        this.consumeAttemptTimeout = Duration.ofMillis(kafkaProperties.getConsumeAttemptTimeout());
    }

    private Consumer<Void, SpecificRecordBase> initAndGetConsumer(String topic) {
        Consumer<Void, SpecificRecordBase> consumer = new KafkaConsumer<>(getConsumerConfig());
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    public List<UserActionAvro> pollUserActions() {
        return pollMessages(actionConsumer, UserActionAvro.class);
    }

    public List<EventSimilarityAvro> pollEventSimilarity() {
        return pollMessages(similarityConsumer, EventSimilarityAvro.class);
    }

    private <T extends SpecificRecordBase> List<T> pollMessages(Consumer<Void, SpecificRecordBase> consumer,
                                                                Class<T> targetClass) {
        List<T> messages = new ArrayList<>();

        try {
            ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(consumeAttemptTimeout);

            for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                if (targetClass.isInstance(record.value())) {
                    messages.add(targetClass.cast(record.value()));
                }
            }

            if (!records.isEmpty()) {
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            log.debug("Consumer wakeup called");
        } catch (Exception e) {
            log.error("Error polling messages from Kafka", e);
        }
        return messages;
    }

    private Properties getConsumerConfig() {
        Properties config = new Properties();
        Map<String, String> props = kafkaProperties.getConsumer().getProperties();
        props.forEach(config::setProperty);
        return config;
    }

    @PreDestroy
    public void stop() {
        if (actionConsumer != null) {
            actionConsumer.close();
            log.info("Kafka action consumer closed");
        }

        if (similarityConsumer != null) {
            similarityConsumer.close();
            log.info("Kafka similarity consumer closed");
        }
    }
}