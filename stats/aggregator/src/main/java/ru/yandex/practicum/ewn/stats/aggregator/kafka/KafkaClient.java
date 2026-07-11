package ru.yandex.practicum.ewn.stats.aggregator.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.ewn.stats.aggregator.kafka.config.KafkaProperties;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaClient {
    private static final long OFFSET_INCREMENT = 1L;

    private final KafkaProperties kafkaProperties;

    private Producer<Void, SpecificRecordBase> producer;
    private Consumer<Void, SpecificRecordBase> consumer;

    private Duration consumeAttemptTimeout;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();


    @PostConstruct
    private void init() {
        initProducer();
        initConsumer();

        this.consumeAttemptTimeout = Duration.ofMillis(kafkaProperties.getConsumeAttemptTimeout());
    }

    private void initProducer() {
        producer = new KafkaProducer<>(getProducerConfig());
        log.info("Kafka producer initialized");
    }

    private void initConsumer() {
        consumer = new KafkaConsumer<>(getConsumerConfig());

        String actionsTopic = kafkaProperties.getTopic().getActions();
        consumer.subscribe(Collections.singletonList(actionsTopic));
        log.info("Kafka consumer subscribed to topic: {}", actionsTopic);
    }

    public void sendToSimilarity(EventSimilarityAvro similarityAvro) {
        String topic = kafkaProperties.getTopic().getSimilarity();
        try {
            producer.send(new ProducerRecord<>(topic, similarityAvro));
        } catch (Exception e) {
            log.error("Failed sending record {} to topic {}", similarityAvro, topic, e);

        }
    }

    public List<UserActionAvro> pollUserActions() {
        List<UserActionAvro> messages = new ArrayList<>();
        try {
            ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(consumeAttemptTimeout);

            for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                messages.add((UserActionAvro) record.value());

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
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error polling messages from Kafka", e);
        }
        return messages;
    }

    public void wakeup(){
        this.consumer.wakeup();
    }

    private Properties getConsumerConfig() {
        Properties config = new Properties();
        Map<String, String> props = kafkaProperties.getConsumer().getProperties();
        props.forEach(config::setProperty);
        return config;
    }

    private Properties getProducerConfig() {
        Properties config = new Properties();
        Map<String, String> props = kafkaProperties.getProducer().getProperties();
        props.forEach(config::setProperty);
        return config;
    }

    @PreDestroy
    public void stop() {
        if (producer != null) {
            producer.flush();
            producer.close();
            log.info("Kafka producer closed");
        }
        if (consumer != null) {
            consumer.close();
            log.info("Kafka consumer closed");
        }
    }
}
