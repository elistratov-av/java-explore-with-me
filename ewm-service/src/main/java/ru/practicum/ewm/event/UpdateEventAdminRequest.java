package ru.practicum.ewm.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventAdminRequest extends UpdateEventRequest {
/*
    private long id; // Идентификатор события

    @Size(min = 20, max = 2000)
    private String annotation; // Краткое описание

    private Long category; // Категория

    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime eventDate; // Дата и время на которые намечено событие

    private Location location; // Локация

    private Boolean paid; // Нужно ли оплачивать участие в событии

    @PositiveOrZero
    private Integer participantLimit; // Ограничение на количество участников

    private Boolean requestModeration; // Нужна ли премодерация заявок на участие

    @Size(min = 3, max = 120)
    private String title; // Заголовок события
*/

    private EventStateAdminAction stateAction = null; // Новое состояние события
}
