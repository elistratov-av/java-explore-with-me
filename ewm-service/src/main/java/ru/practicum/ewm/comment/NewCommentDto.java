package ru.practicum.ewm.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentDto {
    private Long id; // Идентификатор

    @NotBlank
    @Size(min = 5, max = 7000)
    private String text; // Текст комментария

    private long eventId; // Событие

    private long authorId; // Автор комментария
}
