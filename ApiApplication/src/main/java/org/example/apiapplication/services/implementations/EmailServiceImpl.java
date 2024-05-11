package org.example.apiapplication.services.implementations;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.transaction.Transactional;
import org.example.apiapplication.services.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Value("${email_service.api-key}")
    private String apiKey;

    @Value("${email_service.from-email}")
    private String from;

    @Value("${email_service.frontend-url}")
    private String frontendUrl;

    private final String FORGOT_PASSWORD = "Follow the link to reset your password: " +
            "<a href=\"%s\">%s</a>";
    private final String SIGNUP_INVITE = "Complete sign up by following the link: " +
            "<a href=\"%s\">%s</a>";
    private final String APPROVE_USER = "You have been approved on the System. " +
            "Follow the link to sign in: <a href=\"%s\">%s</a>";
    private final String REJECT_USER = "You have been rejected from the System. " +
            "Contact administrator to get details!";
    private final String ACTIVATE_USER = "You have been activated on the System. " +
            "Follow the link to sign in: <a href=\"%s\">%s</a>";
    private final String DEACTIVATE_USER = "You have been deactivated on the System. " +
            "Contact administrator to get details!";

    @Override
    public void forgotPassword(String email, String token) {
        String forgotPasswordUrl = frontendUrl + "/auth/forgotpassword/" + token;
        String body = String.format(FORGOT_PASSWORD, forgotPasswordUrl, forgotPasswordUrl);

        String subject = "Reset Password";
        sendEmail(email, subject, body);
    }

    @Override
    public void signUpWithCode(String email, String code) {
        String signupInvite = frontendUrl + "/auth/signupbyinvitecode/" + code;
        String body = String.format(SIGNUP_INVITE, signupInvite, signupInvite);

        String subject = "Complete Sign Up!";
        sendEmail(email, subject, body);
    }

    @Override
    public void approveUser(String email) {
        String subject = "You have been approved!";
        String loginUrl = frontendUrl + "/auth/signin";
        String body = String.format(APPROVE_USER, loginUrl, loginUrl);

        sendEmail(email, subject, body);
    }

    @Override
    public void rejectUser(String email) {
        String subject = "You have been rejected!";
        sendEmail(email, subject, REJECT_USER);
    }

    @Override
    public void activateUser(String email) {
        String subject = "You have been activated!";
        String loginUrl = frontendUrl + "/auth/signin";
        String body = String.format(ACTIVATE_USER, loginUrl, loginUrl);

        sendEmail(email, subject, body);
    }

    @Override
    public void deactivateUser(String email) {
        String subject = "You have been deactivated!";
        sendEmail(email, subject, DEACTIVATE_USER);
    }

    private void sendEmail(String email, String subject, String body) {
        String fromEmail = from;

        Email from = new Email(fromEmail);
        Email to = new Email(email);
        Content content = new Content("text/html", "<p>" + body + "</p>");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ignored) {
        }
    }
}
