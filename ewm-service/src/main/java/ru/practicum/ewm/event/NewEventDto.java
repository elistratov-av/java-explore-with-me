package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation; // Краткое описание

    @NotNull
    private Long category; // Категория

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; // Полное описание события

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие

    @NotNull
    private Location location; // Локация

    private Boolean paid = false; // Нужно ли оплачивать участие в событии

    @PositiveOrZero
    private Integer participantLimit = 0; // Ограничение на количество участников

    private Boolean requestModeration = true; // Нужна ли пре-модерация заявок на участие

    @NotBlank
    @Size(min = 3, max = 120)
    private String title; // Заголовок события

    @AssertTrue(message = "Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента")
    public boolean isStartBeforeEnd() {
        if (eventDate == null) return false;
        LocalDateTime twoHoursLater = LocalDateTime.now().plusHours(2);
        return eventDate.isAfter(twoHoursLater);
    }
}
