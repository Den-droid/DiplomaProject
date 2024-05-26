package org.example.apiapplication.exceptions.auth;

public class UserWithUsernameNotFoundException extends RuntimeException {
    public UserWithUsernameNotFoundException(String username) {
        super("Користувача з іменем " + username + " не існує!");
    }
}
