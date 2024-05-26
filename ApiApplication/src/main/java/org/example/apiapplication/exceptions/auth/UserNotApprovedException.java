package org.example.apiapplication.exceptions.auth;

public class UserNotApprovedException extends RuntimeException {
    public UserNotApprovedException() {
        super("Ви ще не були підтверджені. Заерніться до адмінстратора!");
    }
}
