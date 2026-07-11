package ru.yandex.practicum.ewn.stats.aggregator.kafka.config;


import lombok.Data;

@Data
public class Topic {
    private String actions;
    private String similarity;
}
