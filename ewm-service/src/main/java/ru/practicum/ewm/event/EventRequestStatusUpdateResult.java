package ru.practicum.ewm.event;

import lombok.Data;
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;

    public void addConfirmedRequest(ParticipationRequestDto r) {
        if (confirmedRequests == null)
            confirmedRequests = new ArrayList<>();
        confirmedRequests.add(r);
    }

    private List<ParticipationRequestDto> rejectedRequests;

    public void addRejectedRequest(ParticipationRequestDto r) {
        if (rejectedRequests == null)
            rejectedRequests = new ArrayList<>();
        rejectedRequests.add(r);
    }
}
