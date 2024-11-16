package ru.practicum.ewm;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ViewStatsDto {
    private String app; // Название сервиса
    private String uri; // URI сервиса
    private long hits; // Количество просмотров
}
