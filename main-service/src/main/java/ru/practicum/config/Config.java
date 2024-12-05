package ru.practicum.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.practicum.client.StatClient;

@Component
@RequiredArgsConstructor
public class Config {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${stats-server.url}")
    private String serverUri;

    @Bean
    public StatClient statClient() {
        return new StatClient(restTemplateBuilder, serverUri);
    }

    @Bean
    public String serverUrl() {
        return serverUri;
    }
}
