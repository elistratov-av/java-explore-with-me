package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class RestStatsClient implements StatsClient {
    private final RestClient restClient;
    private final String serverUrl;

    public RestStatsClient(@Value("${client.url}") String serverUrl) {
        restClient = RestClient.create();
        this.serverUrl = serverUrl;
    }

    @Override
    public void hit(EndpointHitDto endpointHit) {
        try {
            restClient.post()
                    .uri(serverUrl + "/hit")
                    .contentType(APPLICATION_JSON)
                    .body(endpointHit)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Throwable ex) {
            log.warn("Ошибка при вызове метода hit", ex);
        }
    }

    @Override
    public List<ViewStatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        try {
            Optional<List<String>> optUris = uris != null ? Optional.of(Arrays.asList(uris)) : Optional.empty();
            var statsUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParamIfPresent("uris", optUris)
                    .queryParam("unique", unique)
                    .build()
                    .toUri();
            return restClient.get()
                    .uri(statsUri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Throwable ex) {
            log.warn("Ошибка при вызове метода stats", ex);
        }
        return List.of();
    }
}
