package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
    @RestControllerAdvice
    public class ErrorHandler {
        @ExceptionHandler
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
            log.error("Ошибка при вводе значений: {}", e.getMessage());
            return new ErrorResponse(e.getMessage());
        }

        @ExceptionHandler
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ErrorResponse handleNotFound(NotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return new ErrorResponse(e.getMessage());
        }

        @ExceptionHandler
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse handleMethodBadRequest(MethodArgumentNotValidException e) {
            log.error("Некорректные данные от пользователя: {}", e.getMessage());
            return new ErrorResponse(e.getMessage());
        }

        @ExceptionHandler
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse handleException(Exception e) {
            log.error("Произошла ошибка: {}", e.getMessage());
            return new ErrorResponse(e.getMessage());
        }
}
