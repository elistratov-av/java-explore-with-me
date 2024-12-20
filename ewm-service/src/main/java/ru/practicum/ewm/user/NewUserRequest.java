package ru.practicum.ewm.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Данные нового пользователя
@Data
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250)
    private String name; // Имя

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    private String email; // Почтовый адрес
}
