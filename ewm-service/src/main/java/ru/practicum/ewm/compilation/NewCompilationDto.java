package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

// Подборка событий
@Data
public class NewCompilationDto {
    private List<Long> events; // Список идентификаторов событий входящих в подборку

    private Boolean pinned = false; // Закреплена ли подборка на главной странице сайта

    @NotBlank
    @Size(max = 50)
    private String title; // Заголовок подборки
}
