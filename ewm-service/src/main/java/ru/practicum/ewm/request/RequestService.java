package ru.practicum.ewm.request;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findByRequesterId(long requesterId);

    ParticipationRequestDto add(long requesterId, long eventId);

    ParticipationRequestDto cancel(long id, long requesterId);
}
