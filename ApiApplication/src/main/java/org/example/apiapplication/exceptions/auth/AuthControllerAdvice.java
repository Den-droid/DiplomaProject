package org.example.apiapplication.exceptions.auth;

import org.example.apiapplication.dto.BaseExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AuthControllerAdvice {
    @ExceptionHandler(value = UserWithUsernameExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleAuthException(UserWithUsernameExistsException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(UserWithTokenNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithInviteCodeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(UserWithInviteCodeNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserWithUsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseExceptionDto handleAuthException(UserWithUsernameNotFoundException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseExceptionDto handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserNotApprovedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleNotApprovedException(UserNotApprovedException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserNotActiveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleNotActiveException(UserNotActiveException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }

    @ExceptionHandler(value = UserNotSignedUpException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseExceptionDto handleNotSignedUpException(UserNotSignedUpException ex, WebRequest request) {
        return new BaseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());
    }
}
