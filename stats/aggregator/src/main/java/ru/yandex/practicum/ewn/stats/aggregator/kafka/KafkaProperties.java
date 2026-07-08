package ru.yandex.practicum.ewn.stats.aggregator.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaProperties {
    private int consumeAttemptTimeout;
    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();
    private Topic topic = new Topic();

    @Data
    public static class Producer {
        private Map<String, String> properties;
    }

    @Data
    public static class Consumer {
        private Map<String, String> properties;
    }

    @Data
    public static class Topic {
        private String actions;
        private String similarity;
    }
}