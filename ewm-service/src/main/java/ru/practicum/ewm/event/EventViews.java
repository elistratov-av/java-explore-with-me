package ru.practicum.ewm.event;

public interface EventViews {

    // Идентификатор
    Long getId();

    void setId(Long id);

    // Количество просмотров события
    long getViews();

    void setViews(long views);
}
