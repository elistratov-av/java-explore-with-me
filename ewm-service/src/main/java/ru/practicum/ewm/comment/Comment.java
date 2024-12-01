package ru.practicum.ewm.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Идентификатор

    @Column(length = 7000, nullable = false)
    private String text; // Текст комментария

    @Column(nullable = false)
    private LocalDateTime created; // Дата и время создания комментария

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // Событие

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author; // Пользователь, отправивший комментарий
}
