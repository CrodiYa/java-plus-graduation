package ru.yandex.practicum.ewm.stats.collector.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaProperties {
    private Producer producer = new Producer();
    private Topic topic = new Topic();

    @Data
    public static class Producer {
        private Map<String, String> properties;
    }

    @Data
    public static class Topic {
        private String actions;
    }
}