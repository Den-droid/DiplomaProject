package org.example.apiapplication.exceptions.auth;

public class UserWithInviteCodeNotFoundException extends RuntimeException {
    public UserWithInviteCodeNotFoundException() {
        super("Такого коду запрошення не існує!");
    }
}
