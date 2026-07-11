package ru.yandex.practicum.ewn.stats.aggregator.kafka.config;

import lombok.Data;

import java.util.Map;

@Data
public class Consumer {
    private Map<String, String> properties;
}
