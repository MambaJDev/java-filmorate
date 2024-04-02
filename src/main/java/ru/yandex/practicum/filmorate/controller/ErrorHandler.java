package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private String getStacktrace(Exception exception) {
        return Arrays.toString(exception.getStackTrace());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(final MethodArgumentNotValidException exception) {
        log.error("400 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundObject(final NotFoundException exception) {
        log.error("404 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception exception) {
        log.error("500 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage(), getStacktrace(exception));
    }

    @Getter
    @Setter
    private static class ErrorResponse {
        private String error;
        private String stacktrace;

        ErrorResponse(final String error, final String stacktrace) {
            this.error = error;
            this.stacktrace = stacktrace;
        }

        ErrorResponse(final String error) {
            this.error = error;
        }
    }
}