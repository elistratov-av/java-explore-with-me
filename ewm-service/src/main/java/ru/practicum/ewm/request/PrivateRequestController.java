package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> findByRequesterId(@PathVariable long userId) {
        log.info("==> findByRequesterId request: userId = {}", userId);
        List<ParticipationRequestDto> requests = requestService.findByRequesterId(userId);
        log.info("<== findByRequesterId request: count = {}", requests.size());
        return requests;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId) {
        log.info("==> create request: userId = {}, eventId = {}", userId, eventId);
        ParticipationRequestDto request = requestService.add(userId, eventId);
        log.info("<== create request: {}", request);
        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        log.info("==> cancel request: userId = {}, requestId = {}", userId, requestId);
        ParticipationRequestDto request = requestService.cancel(requestId, userId);
        log.info("<== cancel request: {}", request);
        return request;
    }
}
