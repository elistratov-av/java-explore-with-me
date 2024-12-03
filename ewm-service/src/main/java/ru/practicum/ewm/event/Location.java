package ru.practicum.ewm.event;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Location {
    private Float lat;

    private Float lon;
}
