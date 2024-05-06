package org.example.apiapplication.exceptions.profile;

import org.example.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ProfileControllerAdvice {
    @ExceptionHandler(value = ProfileScientistScientometricSystemExists.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleLabelException(ProfileScientistScientometricSystemExists ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }
}
