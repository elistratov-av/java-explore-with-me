package ru.practicum.ewm.user;

import lombok.Data;

// Пользователь
@Data
public class UserDto {
    private Long id; // Идентификатор

    private String name; // Имя

    private String email; // Почтовый адрес
}
