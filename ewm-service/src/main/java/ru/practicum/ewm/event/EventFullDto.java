package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto implements EventStats {
    private Long id; // Идентификатор

    private String annotation; // Краткое описание

    private CategoryDto category; // Категория

    private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn; // Дата и время создания события

    private String description; // Полное описание события

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие

    private UserShortDto initiator; // Инициатор

    private Location location; // Локация

    private Boolean paid; // Нужно ли оплачивать участие в событии

    private Integer participantLimit = 0; // Ограничение на количество участников

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn; // Дата и время публикации события

    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    private EventState state; // Состояние жизненного цикла события

    private String title; // Заголовок события

    private long views; // Количество просмотров события

    private long countComments; // Количество комментариев по событию
}
