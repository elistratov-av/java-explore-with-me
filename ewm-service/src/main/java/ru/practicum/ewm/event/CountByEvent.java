package ru.practicum.ewm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CountByEvent {
    private Long eventId;
    private Long count;
}
