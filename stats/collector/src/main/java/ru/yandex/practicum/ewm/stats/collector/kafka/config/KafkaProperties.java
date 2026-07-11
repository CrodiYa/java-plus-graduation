package ru.yandex.practicum.ewm.stats.collector.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaProperties {
    private Producer producer = new Producer();
    private Topic topic = new Topic();
}
