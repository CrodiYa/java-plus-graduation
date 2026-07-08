package ru.yandex.practicum.ewm.stats.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.ewm.stats.collector.kafka.KafkaProperties;

@SpringBootApplication
public class CollectorApplication {
    public static void main(String[] args) {
//        SpringApplication.run(CollectorApplication.class, args);
        ConfigurableApplicationContext context = SpringApplication.run(CollectorApplication.class, args);

        // Проверяем, что бин создался
        KafkaProperties kafkaProperties = context.getBean(KafkaProperties.class);
        System.out.println("KafkaProperties bean: " + kafkaProperties);
        System.out.println("Producer: " + kafkaProperties.getProducer());
        System.out.println("Properties: " + kafkaProperties.getProducer().getProperties());
        System.out.println("Topic: " + kafkaProperties.getTopic());
        System.out.println("Actions topic: " + kafkaProperties.getTopic().getActions());
    }
}
