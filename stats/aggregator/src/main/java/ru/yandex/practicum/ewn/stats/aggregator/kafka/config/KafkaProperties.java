package ru.yandex.practicum.ewn.stats.aggregator.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaProperties {
    private int consumeAttemptTimeout;
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();
    private Topic topic = new Topic();
}
