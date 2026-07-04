package ru.yandex.practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.client")
@SpringBootApplication
@ComponentScan(basePackages = {"ru.yandex.practicum.event", "ru.yandex.practicum.interaction", "ru.yandex.practicum.client"})
public class EventApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class);
    }
}
