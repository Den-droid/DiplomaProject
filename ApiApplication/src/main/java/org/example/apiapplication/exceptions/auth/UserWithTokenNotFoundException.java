package org.example.apiapplication.exceptions.auth;

public class UserWithTokenNotFoundException extends RuntimeException {
    public UserWithTokenNotFoundException() {
        super("Такого токену не існує!");
    }
}
