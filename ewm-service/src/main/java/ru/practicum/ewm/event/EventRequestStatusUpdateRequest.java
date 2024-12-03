package ru.practicum.ewm.event;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    private RequestStatusAction status;
}
