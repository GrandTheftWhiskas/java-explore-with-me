package ru.practicum.client;

import jakarta.annotation.Nullable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.EventDto;
import ru.practicum.StatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatClient extends BaseClient {
    public StatClient(RestTemplateBuilder builder, String serverUrl) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<List<StatsDto>> getStat(LocalDateTime start, LocalDateTime end,
                                                  @Nullable String uris, boolean unique) {
        String encodedStartDate = encodeParameter(convertLocalDateTimeToString(start));
        String encodedEndDate = encodeParameter(convertLocalDateTimeToString(end));

        Map<String, Object> parameters = new HashMap<>(
                Map.of(
                        "start", encodedStartDate,
                        "end", encodedEndDate,
                        "unique", unique
                )
        );
        if (uris != null) {
            parameters.put("uris", uris);
        }
        return getList("/stats" + "?start={start}&end={end}&uris={uris}&unique={unique}", parameters,
                new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Object> addStat(EventDto dto) {
        System.out.println("клиент");
        return post("/hit", dto, null);
    }

    private String encodeParameter(String parameter) {
        return URLEncoder.encode(parameter, StandardCharsets.UTF_8);
    }

    private String convertLocalDateTimeToString(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
