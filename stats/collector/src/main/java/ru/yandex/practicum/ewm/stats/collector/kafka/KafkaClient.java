package ru.yandex.practicum.ewm.stats.collector.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Map;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaClient {

    private final KafkaProperties kafkaProperties;
    private Producer<Void, SpecificRecordBase> producer;

    @PostConstruct
    private void initProducer() {
        producer = new KafkaProducer<>(getProducerConfig());
    }

    public void send(UserActionAvro actionAvro) {
        String topic = kafkaProperties.getTopic().getActions();
        producer.send(new ProducerRecord<>(topic, actionAvro));
    }

    private Properties getProducerConfig() {
        Properties config = new Properties();
        Map<String, String> props = kafkaProperties.getProducer().getProperties();
        props.forEach(config::setProperty);

        return config;
    }

    public void stop() {
        if (producer != null) {
            producer.close();
        }
    }
}
