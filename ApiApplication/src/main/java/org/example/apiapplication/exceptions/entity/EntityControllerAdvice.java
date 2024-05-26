package org.example.apiapplication.exceptions.entity;

import org.example.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class EntityControllerAdvice {
    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleEntityException(EntityNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = EntityWithIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(EntityWithIdNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }
}
