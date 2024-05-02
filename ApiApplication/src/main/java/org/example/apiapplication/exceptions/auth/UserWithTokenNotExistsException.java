package org.example.apiapplication.exceptions.auth;

public class UserWithTokenNotExistsException extends RuntimeException {
    public UserWithTokenNotExistsException() {
        super("Такого токену не існує!");
    }
}
