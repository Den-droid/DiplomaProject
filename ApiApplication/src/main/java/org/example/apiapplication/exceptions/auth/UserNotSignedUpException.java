package org.example.apiapplication.exceptions.auth;

public class UserNotSignedUpException extends RuntimeException{
    public UserNotSignedUpException() {
        super("Finish your signing up!");
    }

}
