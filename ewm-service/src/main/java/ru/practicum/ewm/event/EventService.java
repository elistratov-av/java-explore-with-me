package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.data.domain.PageRollRequest;
import ru.practicum.ewm.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventFullDto add(long initiatorId, NewEventDto newEvent);

    EventFullDto findByIdForInitiator(long eventId, long initiatorId);

    EventFullDto update(long initiatorId, UpdateEventUserRequest newEvent);

    EventFullDto update(UpdateEventAdminRequest newEvent);

    List<EventShortDto> findByInitiatorId(long initiatorId, Pageable pageable);

    List<EventFullDto> findBy(AdminSearchParams params, Pageable pageable);

    List<EventShortDto> findPublishedBy(SearchParams params, PageRollRequest pageable);

    EventFullDto findPublishedById(long id);

    List<ParticipationRequestDto> findByIdRequests(long initiatorId, long eventId);

    EventRequestStatusUpdateResult update(long initiatorId, long eventId, EventRequestStatusUpdateRequest newStatusRequest);
}
