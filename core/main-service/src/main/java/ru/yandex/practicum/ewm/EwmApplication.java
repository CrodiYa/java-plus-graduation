package ru.yandex.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"ru.yandex.practicum.ewm", "ru.yandex.practicum.client"})
public class EwmApplication {
    public static void main(String[] args) {
        SpringApplication.run(EwmApplication.class, args);
    }
}