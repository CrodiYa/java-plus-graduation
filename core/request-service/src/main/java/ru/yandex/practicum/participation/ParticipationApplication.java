package ru.yandex.practicum.participation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction.client")
@SpringBootApplication
@ComponentScan(basePackages = {"ru.yandex.practicum.participation", "ru.yandex.practicum.interaction"})
public class ParticipationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParticipationApplication.class);
    }
}
