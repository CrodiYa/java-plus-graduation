package ru.yandex.practicum.ewn.stats.aggregator.kafka.config;

import lombok.Data;

import java.util.Map;

@Data
public class Producer {
    private Map<String, String> properties;
}
