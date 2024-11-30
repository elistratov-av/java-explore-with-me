package ru.practicum.ewm.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Идентификатор

    @Column(length = 2000)
    private String annotation; // Краткое описание

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Категория

    @Column(nullable = false)
    private LocalDateTime created; // Дата и время создания события

    @Column(length = 7000, nullable = false)
    private String description; // Полное описание события

    @Column(nullable = false)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator; // Инициатор

    @Embedded
    private Location location; // Локация

    @Column(nullable = false)
    private Boolean paid; // Нужно ли оплачивать участие в событии

    @Column(name = "participant_limit")
    private Integer participantLimit; // Ограничение на количество участников

    @Column
    private LocalDateTime published; // Дата и время публикации события

    @Column(name = "request_moderation", nullable = false)
    // Нужна ли пре-модерация заявок на участие
    // Если true, то все заявки будут ожидать подтверждения инициатором события
    // Если false - то будут подтверждаться автоматически
    private Boolean requestModeration;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private EventState state; // Состояние жизненного цикла события

    @Column(length = 120)
    private String title; // Заголовок события

    @OneToMany(mappedBy = "event")
    private List<Request> requests = new ArrayList<>();
}
