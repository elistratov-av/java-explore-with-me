package ru.practicum.ewm.event;

public interface EventConfirmedRequests {

    // Идентификатор
    Long getId();
    void setId(Long id);

    // Количество одобренных заявок на участие в данном событии
    Long getConfirmedRequests();
    void setConfirmedRequests(Long confirmedRequests);
}
