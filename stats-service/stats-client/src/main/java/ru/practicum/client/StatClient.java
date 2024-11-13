package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.EventDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatClient extends BaseClient {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatClient(RestTemplateBuilder builder, String serverUrl) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> post(EventDto eventDto) {
        return post("/hit", eventDto);
    }

    public ResponseEntity<Object> get(Long eventId) {
        return get("/stats/" + eventId);
    }

    public ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, boolean unique) {
        Map<String, Object> components;
        if (uris != null) {
             components = Map.of(
                    "start", URLEncoder.encode(formatter.format(start), StandardCharsets.UTF_8),
                    "end", URLEncoder.encode(formatter.format(end), StandardCharsets.UTF_8),
                    "uris", uris,
                    "unique", unique
            );
        } else {
             components = Map.of(
                    "start", URLEncoder.encode(formatter.format(start), StandardCharsets.UTF_8),
                    "end", URLEncoder.encode(formatter.format(end), StandardCharsets.UTF_8),
                    "unique", unique
            );
        }

        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", null, components);
    }



}
