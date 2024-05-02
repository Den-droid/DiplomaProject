package org.example.apiapplication.exceptions.auth;

public class UserWithInviteCodeNotExistsException extends RuntimeException {
    public UserWithInviteCodeNotExistsException() {
        super("Такого коду запрошення не існує!");
    }
}
