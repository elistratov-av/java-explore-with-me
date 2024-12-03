package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> findByRequesterId(long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + requesterId + " не найден"));
        return requestMapper.map(requestRepository.findByRequesterId(requesterId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto add(long requesterId, long eventId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + requesterId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));

        // проверяем необходимые условия
        if (event.getInitiator().getId() == requesterId)
            throw new DataIntegrityViolationException("Инициатор события не может добавить запрос на участие в своём событии");
        if (event.getState() != EventState.PUBLISHED)
            throw new DataIntegrityViolationException("Нельзя участвовать в неопубликованном событии");
        if (event.getParticipantLimit() > 0) {
            long confirmedRequests = requestRepository.countByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED);
            if (event.getParticipantLimit() <= confirmedRequests)
                throw new DataIntegrityViolationException("У события достигнут лимит запросов на участие");
        }

        // если все условия соблюдены, создаем заявку
        Request r = new Request();
        r.setCreated(LocalDateTime.now());
        r.setRequester(requester);
        r.setEvent(event);
        r.setStatus(event.getRequestModeration() && event.getParticipantLimit() > 0 ? RequestStatus.PENDING : RequestStatus.CONFIRMED);
        Request request = requestRepository.save(r);
        return requestMapper.map(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long id, long requesterId) {
        Request r = requestRepository.findByIdAndRequesterId(id, requesterId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + id + " не найден"));

        r.setStatus(RequestStatus.CANCELED);
        Request request = requestRepository.save(r);
        return requestMapper.map(request);
    }
}
