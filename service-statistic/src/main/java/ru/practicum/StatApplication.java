package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class StatApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatApplication.class, args);
    }
}
