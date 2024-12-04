package ru.practicum.ewm.event;

public interface EventCountComments {

    // Идентификатор
    Long getId();

    void setId(Long id);

    // Количество комментариев к событию
    long getCountComments();

    void setCountComments(long views);
}
