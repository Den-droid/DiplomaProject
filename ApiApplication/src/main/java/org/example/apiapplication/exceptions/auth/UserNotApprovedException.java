package org.example.apiapplication.exceptions.auth;

public class UserNotApprovedException extends RuntimeException {
    public UserNotApprovedException() {
        super("You have not been approved yet. Contact administrator!");
    }
}
