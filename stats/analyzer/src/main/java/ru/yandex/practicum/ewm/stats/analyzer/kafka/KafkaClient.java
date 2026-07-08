package ru.yandex.practicum.ewm.stats.analyzer.kafka;

import java.util.List;
import java.util.Properties;

public interface KafkaClient<T> {

    void init();

    List<T> pollMessages ();

    void wakeup ();

    void stop ();

    Properties getConfig();

}
