package ru.yandex.practicum.ewm.stats.analyzer.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "analyzer.kafka")
public class KafkaProperties {
    private int consumeAttemptTimeout;
    private ConsumerConfig actionConsumer = new ConsumerConfig();
    private ConsumerConfig similarityConsumer = new ConsumerConfig();
    private Topic topic = new Topic();
}
