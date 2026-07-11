package ru.yandex.practicum.ewm.stats.analyzer.kafka.config;

import lombok.Data;

import java.util.Map;

@Data
public class ConsumerConfig {
    private Map<String, String> properties;
}
