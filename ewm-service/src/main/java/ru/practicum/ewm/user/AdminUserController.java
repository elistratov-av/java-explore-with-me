package ru.practicum.ewm.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.data.domain.PageRollRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll(
            @RequestParam(required = false) Long[] ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("==> findAll users: ids = {}, from = {}, size = {}", ids, from, size);
        List<UserDto> users = userService.findAll(ids, PageRollRequest.of(from, size));
        log.info("<== findAll users: count = {}", users.size());
        return users;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserRequest newUser) {
        log.info("==> create user: {}", newUser);
        UserDto user = userService.add(newUser);
        log.info("<== create user: {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.info("==> delete user: userId = {}", userId);
        userService.delete(userId);
        log.info("<== delete user");
    }
}
