package ru.yandex.practicum.ewm.stats.analyzer.kafka.config;

import lombok.Data;

@Data
public class Topic {
    private String actions;
    private String similarity;
}