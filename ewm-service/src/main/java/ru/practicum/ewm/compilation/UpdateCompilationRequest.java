package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

// Изменение информации о подборке событий. Если поле в запросе не указано (равно null)
// - значит изменение этих данных не требуется
@Data
public class UpdateCompilationRequest {
    private long id; // Идентификатор подборки

    private List<Long> events; // Список id событий подборки для полной замены текущего списка

    private Boolean pinned; // Закреплена ли подборка на главной странице сайта

    @Size(max = 50)
    private String title; // Заголовок подборки
}
