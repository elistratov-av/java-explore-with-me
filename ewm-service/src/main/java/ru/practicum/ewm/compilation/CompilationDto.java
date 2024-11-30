package ru.practicum.ewm.compilation;

import lombok.Data;
import ru.practicum.ewm.event.EventShortDto;

import java.util.List;

// Подборка событий
@Data
public class CompilationDto {
    private List<EventShortDto> events; // Список событий входящих в подборку

    private Long id; // Идентификатор

    private Boolean pinned; // Закреплена ли подборка на главной странице сайта

    private String title; // Заголовок подборки
}
