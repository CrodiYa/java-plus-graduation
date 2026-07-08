package ru.yandex.practicum.ewn.stats.aggregator.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
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

    private Producer<Void, SpecificRecordBase> producer;
    private Consumer<Void, SpecificRecordBase> consumer;

    private Duration consumeAttemptTimeout;

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
                if (record.value() instanceof UserActionAvro) {
                    messages.add((UserActionAvro) record.value());
                }
            }

            if (!records.isEmpty()) {
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

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