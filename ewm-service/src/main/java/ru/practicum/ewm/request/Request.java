package ru.practicum.ewm.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "requests")
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Идентификатор

    @Column(nullable = false)
    private LocalDateTime created; // Дата и время создания запроса

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // Событие

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester; // Пользователь, отправивший заявку

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private RequestStatus status; // Статус заявки
}
