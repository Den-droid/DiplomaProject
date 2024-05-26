package org.example.apiapplication.exceptions.auth;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Невдало для [%s]: %s", token, message));
    }
}
