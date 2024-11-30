package ru.practicum.ewm.user;

import lombok.Data;

// Пользователь (краткая информация)
@Data
public class UserShortDto {
    private Long id; // Идентификатор

    private String name; // Имя
}
