package ru.yandex.practicum.ewm.stats.collector.kafka.config;

import lombok.Data;

import java.util.Map;

@Data
public class Producer {
    private Map<String, String> properties;
}
