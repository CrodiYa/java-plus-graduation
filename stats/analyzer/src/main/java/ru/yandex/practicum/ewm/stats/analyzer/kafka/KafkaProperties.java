package ru.yandex.practicum.ewm.stats.analyzer.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "analyzer.kafka")
public class KafkaProperties {
    private int consumeAttemptTimeout;
    private Consumer consumer = new Consumer();
    private Topic topic = new Topic();


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