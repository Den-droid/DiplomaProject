package org.example.apiapplication.exceptions.auth;

public class UserWithUsernameExistsException extends RuntimeException {
    public UserWithUsernameExistsException(String username) {
        super("Користувач з іменем " + username + " вже існує!");
    }
}
