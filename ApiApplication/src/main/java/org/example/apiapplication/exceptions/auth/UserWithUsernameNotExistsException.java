package org.example.apiapplication.exceptions.auth;

public class UserWithUsernameNotExistsException extends RuntimeException {
    public UserWithUsernameNotExistsException(String username) {
        super("Користувача з іменем " + username + " не існує!");
    }
}
