package ru.yandex.practicum.stats.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ViewStatsServerApp {
    public static void main(String[] args) {
        SpringApplication.run(ViewStatsServerApp.class, args);
    }
}
