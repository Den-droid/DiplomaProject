package org.example.apiapplication.services.interfaces;

public interface EmailService {
    void forgotPassword(String email, String token);

    void signUpWithCode(String email, String code);

    void approveUser(String email);

    void rejectUser(String email);

    void activateUser(String email);

    void deactivateUser(String email);
}
