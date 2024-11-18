package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EndpointHitDto {
    //private Long id; // Идентификатор записи
    @NotBlank
    @Size(max = 255)
    private String app; // Идентификатор сервиса для которого записывается информация
    @NotBlank
    @Size(max = 1024)
    private String uri; // URI для которого был осуществлен запрос
    @Size(max = 20)
    private String ip; // IP-адрес пользователя, осуществившего запрос
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
