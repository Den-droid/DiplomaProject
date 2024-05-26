package org.example.apiapplication.exceptions.auth;

public class UserNotActiveException extends RuntimeException{
    public UserNotActiveException() {
        super("Ви були деактивовані! Зверніться до адміністратора");
    }
}
