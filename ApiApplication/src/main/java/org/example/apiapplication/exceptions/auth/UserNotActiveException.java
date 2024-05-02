package org.example.apiapplication.exceptions.auth;

public class UserNotActiveException extends RuntimeException{
    public UserNotActiveException() {
        super("You were deactivated! Contact administrator!");
    }
}
