package ru.practicum.ewm.error;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private ApiError handleError(String error, HttpStatus status, String reason) {
        log.warn(error);
        return new ApiError(
                status,
                reason,
                error
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Exception e) {
        log.warn("Error", e);
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ошибка приложения",
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NoResourceFoundException e) {
        return handleError(e.getMessage(), HttpStatus.NOT_FOUND, "Искомый объект не был найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MissingServletRequestParameterException e) {
        return handleError(e.getMessage(), HttpStatus.BAD_REQUEST, "Валидация");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final ValidationException e) {
        return handleError(e.getMessage(), HttpStatus.BAD_REQUEST, "Валидация");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MethodArgumentNotValidException e) {
        return handleError(e.getMessage(), HttpStatus.BAD_REQUEST, "Валидация");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final ConditionsNotMetException e) {
        return handleError(e.getMessage(), HttpStatus.BAD_REQUEST, "Для запрошенной операции условия не выполнены");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final DataIntegrityViolationException e) {
        return handleError(e.getMessage(), HttpStatus.CONFLICT, "Нарушено ограничение целостности");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NotFoundException e) {
        return handleError(e.getMessage(), HttpStatus.NOT_FOUND, "Искомый объект не был найден");
    }
}
