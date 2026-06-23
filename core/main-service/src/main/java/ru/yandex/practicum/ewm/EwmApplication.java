package ru.yandex.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interaction.stats.controller.StatsClient;

@SpringBootApplication
@EnableFeignClients(clients = StatsClient.class)
@EnableDiscoveryClient
public class EwmApplication {

    public static void main(String[] args) {
        SpringApplication.run(EwmApplication.class, args);
    }
}