package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EwmServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EwmServiceApp.class, args);

        // Тестовый прогон обращений к серверу статистики
/*
        StatsClient client = ctx.getBean(StatsClient.class);

        EndpointHitDto endpointHit = new EndpointHitDto();
        endpointHit.setApp("ewm-main-service");
        endpointHit.setUri("/events/10");
        endpointHit.setIp("127.0.0.1");
        endpointHit.setTimestamp(LocalDateTime.now());
        client.hit(endpointHit);

        LocalDateTime start = LocalDateTime.of(2024, 11, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 11, 25, 0, 0);
        String[] uris = new String[] {"/events/1", "/events/10"};
        List<ViewStatsDto> stats = client.stats(start, end, uris, false);
        System.out.println("stats: " + stats);
*/
    }
}
