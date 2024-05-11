package org.example.apiapplication.exceptions.auth;

public class UserAlreadyForgotPasswordException extends RuntimeException{
    public UserAlreadyForgotPasswordException(){
        super("You have already asked for resetting password! Search for link in sent email!");
    }
}
