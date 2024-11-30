package ru.practicum.ewm.event;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.data.domain.PageRollRequest;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.ParticipationRequestDto;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.RequestCountByEvent;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    // region Stats

    private final StatsClient statsClient;

    private void mergeHits(EventViews event) {
        LocalDateTime cur = LocalDateTime.now();

        String uri = "/events/" + event.getId();
        List<ViewStatsDto> stats = statsClient.stats(cur.minusMonths(1), cur.plusMinutes(1), new String[] {uri}, true);
        if (stats != null && !stats.isEmpty()) {
            ViewStatsDto s = stats.getFirst();
            event.setViews(s.getHits());
        }
    }

    private void mergeHits(List<? extends EventViews> events) {
        LocalDateTime cur = LocalDateTime.now();

        Map<String, EventViews> eventMap = new HashMap<>();
        for (EventViews e : events) {
            String uri = "/events/" + e.getId();
            eventMap.put(uri, e);
        }

        Set<String> uris = eventMap.keySet();
        List<ViewStatsDto> stats = statsClient.stats(cur.minusMonths(1), cur.plusMinutes(1), uris.toArray(new String[0]), true);
        if (stats != null && !stats.isEmpty()) {
            for (ViewStatsDto s : stats) {
                String uri = s.getUri();
                long hits = s.getHits();
                EventViews e = eventMap.get(uri);
                if (e != null)
                    e.setViews(hits);
            }
        }
    }

    private void mergeConfirmedRequests(EventConfirmedRequests event) {
        event.setConfirmedRequests(
                requestRepository.countByEventIdAndStatusIs(event.getId(), RequestStatus.CONFIRMED)
        );
    }

    private void mergeConfirmedRequests(List<? extends EventConfirmedRequests> events) {
        List<Long> eventIds = events.stream().map(EventConfirmedRequests::getId).collect(Collectors.toList());
        List<RequestCountByEvent> counts = requestRepository
                .countByStatusRequests(eventIds, RequestStatus.CONFIRMED);
        Map<Long, Long> countByEvents = counts.stream()
                .collect(Collectors.toMap(RequestCountByEvent::getEventId, RequestCountByEvent::getCount));
        for (EventConfirmedRequests e : events) {
            e.setConfirmedRequests(countByEvents.getOrDefault(e.getId(), 0L));
        }
    }

    private <T extends EventStats> T mergeAllStats(T event) {
        mergeHits(event);
        mergeConfirmedRequests(event);
        return event;
    }

    private <T extends EventStats> List<T> mergeAllStats(List<T> events) {
        mergeHits(events);
        mergeConfirmedRequests(events);
        return events;
    }

    // endregion

    @Override
    @Transactional
    public EventFullDto add(long initiatorId, NewEventDto newEvent) {
        Category category = categoryRepository.findById(newEvent.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + newEvent.getCategory() + " не найдена"));
        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + initiatorId + " не найден"));
        Event e = eventMapper.map(newEvent, category, initiator);
        e.setCreated(LocalDateTime.now());
        e.setState(EventState.PENDING);
        Event event = eventRepository.save(e);
        return mergeAllStats(eventMapper.map(event));
    }

    @Override
    public EventFullDto findByIdForInitiator(long eventId, long initiatorId) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + initiatorId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId +
                        " для инициатора с id = " + initiatorId + " не найдено"));
        return mergeAllStats(eventMapper.map(event));
    }

    private static boolean isEarlierThan(LocalDateTime dateTime, LocalDateTime other) {
        if (dateTime == null || other == null) return false;
        return dateTime.isBefore(other);
    }

    @Override
    @Transactional
    public EventFullDto update(long initiatorId, UpdateEventUserRequest newEvent) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + initiatorId + " не найден"));
        Event oldEvent = eventRepository.findByIdAndInitiatorId(newEvent.getId(), initiatorId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + newEvent.getId() +
                        " для инициатора с id = " + initiatorId + " не найдено"));
        // проверяем необходимые условия
        switch (oldEvent.getState()) {
            case EventState.CANCELED: case EventState.PENDING:
                break;
            default:
                throw new DataIntegrityViolationException("Изменить можно только отмененные события" +
                        " или события в состоянии ожидания модерации");
        }
        LocalDateTime twoHoursLater = LocalDateTime.now().plusHours(2);
        if (isEarlierThan(oldEvent.getEventDate(), twoHoursLater) ||
                isEarlierThan(newEvent.getEventDate(), twoHoursLater))
            throw new ConditionsNotMetException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");

        // если событие найдено и все условия соблюдены, обновляем его содержимое
        if (StringUtils.isNoneBlank(newEvent.getAnnotation()))
            oldEvent.setAnnotation(newEvent.getAnnotation());
        if (newEvent.getCategory() != null) {
            Category category = categoryRepository.findById(newEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id = " + newEvent.getCategory() + " не найдена"));
            oldEvent.setCategory(category);
        }
        if (StringUtils.isNoneBlank(newEvent.getDescription()))
            oldEvent.setDescription(newEvent.getDescription());
        if (newEvent.getEventDate() != null)
            oldEvent.setEventDate(newEvent.getEventDate());
        if (newEvent.getLocation() != null)
            oldEvent.setLocation(newEvent.getLocation());
        if (newEvent.getPaid() != null)
            oldEvent.setPaid(newEvent.getPaid());
        if (newEvent.getParticipantLimit() != null)
            oldEvent.setParticipantLimit(newEvent.getParticipantLimit());
        if (newEvent.getRequestModeration() != null)
            oldEvent.setRequestModeration(newEvent.getRequestModeration());
        if (newEvent.getStateAction() != null) {
            switch (newEvent.getStateAction()) {
                case EventStateUserAction.CANCEL_REVIEW:
                    oldEvent.setPublished(null);
                    oldEvent.setState(EventState.CANCELED);
                    break;
                case EventStateUserAction.SEND_TO_REVIEW:
                    oldEvent.setPublished(null);
                    oldEvent.setState(EventState.PENDING);
                    break;
            }
        }
        if (StringUtils.isNoneBlank(newEvent.getTitle()))
            oldEvent.setTitle(newEvent.getTitle());

        Event event = eventRepository.save(oldEvent);
        return mergeAllStats(eventMapper.map(event));
    }

    @Override
    @Transactional
    public EventFullDto update(UpdateEventAdminRequest newEvent) {
        Event oldEvent = eventRepository.findById(newEvent.getId())
                .orElseThrow(() -> new NotFoundException("Событие с id = " + newEvent.getId() + " не найдено"));
        // проверяем необходимые условия
        switch (oldEvent.getState()) {
            case EventState.CANCELED: case EventState.PENDING:
                break;
            default:
                throw new DataIntegrityViolationException("Изменить можно только отмененные события" +
                        " или события в состоянии ожидания модерации");
        }

        // если событие найдено и все условия соблюдены, обновляем его содержимое
        if (StringUtils.isNoneBlank(newEvent.getAnnotation()))
            oldEvent.setAnnotation(newEvent.getAnnotation());
        if (newEvent.getCategory() != null) {
            Category category = categoryRepository.findById(newEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id = " + newEvent.getCategory() + " не найдена"));
            oldEvent.setCategory(category);
        }
        if (StringUtils.isNoneBlank(newEvent.getDescription()))
            oldEvent.setDescription(newEvent.getDescription());
        if (newEvent.getEventDate() != null)
            oldEvent.setEventDate(newEvent.getEventDate());
        if (newEvent.getLocation() != null)
            oldEvent.setLocation(newEvent.getLocation());
        if (newEvent.getPaid() != null)
            oldEvent.setPaid(newEvent.getPaid());
        if (newEvent.getParticipantLimit() != null)
            oldEvent.setParticipantLimit(newEvent.getParticipantLimit());
        if (newEvent.getRequestModeration() != null)
            oldEvent.setRequestModeration(newEvent.getRequestModeration());
        if (newEvent.getStateAction() != null) {
            switch (newEvent.getStateAction()) {
                case EventStateAdminAction.PUBLISH_EVENT: {
                    if (oldEvent.getState() != EventState.PENDING)
                        throw new DataIntegrityViolationException("Событие можно публиковать," +
                                " только если оно в состоянии ожидания публикации");
                    LocalDateTime publishedDate = LocalDateTime.now();
                    if (isEarlierThan(oldEvent.getEventDate(), publishedDate.plusHours(1)))
                        throw new ConditionsNotMetException("Дата начала изменяемого события должна быть" +
                                " не ранее чем за час от даты публикации");
                    oldEvent.setPublished(publishedDate);
                    oldEvent.setState(EventState.PUBLISHED);
                }
                break;
                case EventStateAdminAction.REJECT_EVENT: {
                    if (oldEvent.getState() == EventState.PUBLISHED)
                        throw new ConditionsNotMetException("Событие можно отклонить, только если оно еще не опубликовано");
                    oldEvent.setPublished(null);
                    oldEvent.setState(EventState.CANCELED);
                }
                break;
            }
        }
        if (StringUtils.isNoneBlank(newEvent.getTitle()))
            oldEvent.setTitle(newEvent.getTitle());

        Event event = eventRepository.save(oldEvent);
        return mergeAllStats(eventMapper.map(event));
    }

    @Override
    public List<EventShortDto> findByInitiatorId(long initiatorId, Pageable pageable) {
        userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + initiatorId + " не найден"));

        Slice<Event> events = eventRepository.findByInitiatorId(initiatorId, pageable);
        return mergeAllStats(eventMapper.mapShort(events.getContent()));
    }

    @Override
    public List<EventFullDto> findBy(AdminSearchParams params, Pageable pageable) {
        Predicate predicate = params.toPredicate();
        Slice<Event> events = (predicate != null) ?
                eventRepository.findAll(predicate, pageable) :
                eventRepository.findAll(pageable);
        return mergeAllStats(eventMapper.mapFull(events.getContent()));
    }

    private List<EventShortDto> pageAndSortbyViews(List<EventShortDto> events, Pageable pageable) {
        mergeHits(events);
        return events.stream()
                .sorted(Comparator.comparingLong(EventShortDto::getViews))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> findPublishedBy(SearchParams params, PageRollRequest pageable) {
        Predicate predicate = params.toPredicate();
        if (params.getSort() == EventSort.VIEWS) {
            Iterable<Event> events = (predicate != null) ?
                    eventRepository.findAll(predicate) :
                    eventRepository.findAll();
            List<EventShortDto> resultEvents = pageAndSortbyViews(eventMapper.mapShort(events), pageable);
            mergeConfirmedRequests(resultEvents);
            return resultEvents;
        }

        if (params.getSort() == EventSort.EVENT_DATE)
            pageable = pageable.withSort(Sort.Direction.ASC, "eventDate");
        Slice<Event> events = (predicate != null) ?
                eventRepository.findAll(predicate, pageable) :
                eventRepository.findAll(pageable);
        List<EventShortDto> resultEvents = eventMapper.mapShort(events.getContent());
        mergeConfirmedRequests(resultEvents);
        return resultEvents;
    }

    @Override
    public EventFullDto findPublishedById(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено"));
        LocalDateTime published = event.getPublished();
        if (published == null || published.isAfter(LocalDateTime.now()))
            throw new NotFoundException("Событие с id = " + id + " не опубликовано");
        EventFullDto resultEvent = eventMapper.map(event);
        mergeConfirmedRequests(resultEvent);
        return resultEvent;
    }

    @Override
    public List<ParticipationRequestDto> findByIdRequests(long initiatorId, long eventId) {
        eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId +
                        " для инициатора с id = " + initiatorId + " не найдено"));
        return requestMapper.map(requestRepository.findByEventId(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult update(long initiatorId, long eventId, EventRequestStatusUpdateRequest newStatusRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, initiatorId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId +
                        " для инициатора с id = " + initiatorId + " не найдено"));

        List<Request> requests = requestRepository.findByIdIn(newStatusRequest.getRequestIds());
        if (requests.stream().anyMatch(r -> r.getStatus() != RequestStatus.PENDING))
            throw new DataIntegrityViolationException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (requests.isEmpty()) return result;

        if (newStatusRequest.getStatus() == RequestStatusAction.REJECTED) {
            for (Request r : requests) {
                r.setStatus(RequestStatus.REJECTED);
                Request request = requestRepository.save(r);
                result.addRejectedRequest(requestMapper.map(request));
            }
            return result;
        }

        int maxConfirmCount = requests.size();
        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            long confirmedRequests = requestRepository.countByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED);
            int c = (int) (event.getParticipantLimit() - confirmedRequests);
            if (c <= 0)
                throw new DataIntegrityViolationException("У события достигнут лимит запросов на участие");
            if (c < maxConfirmCount)
                maxConfirmCount = c;
        }

        for (int i = 0; i < maxConfirmCount; ++i) {
            Request r = requests.get(i);
            r.setStatus(RequestStatus.CONFIRMED);
            Request request = requestRepository.save(r);
            result.addConfirmedRequest(requestMapper.map(request));
        }

        int count = requests.size();
        for (int i = maxConfirmCount; i < count; ++i) {
            Request r = requests.get(i);
            r.setStatus(RequestStatus.REJECTED);
            Request request = requestRepository.save(r);
            result.addRejectedRequest(requestMapper.map(request));
        }

        return result;
    }
}
