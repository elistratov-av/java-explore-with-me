package ru.practicum.ewm.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id; // Идентификатор категории

    @NotBlank
    @Size(max = 50)
    private String name; // Название категории
}
