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

    private final static String FORGOT_PASSWORD_SUBJECT = "Відновлення паролю";
    private final static String FORGOT_PASSWORD_BODY = "Перейдіть за посиланням, щоб відновити пароль: " +
            "<a href=\"%s\">%s</a>";

    private final static String SIGNUP_INVITE_SUBJECT = "Завершіть реєстрацію!";
    private final static String SIGNUP_INVITE_BODY = "Завершіть реєстрацію перейшовши за посиланням: " +
            "<a href=\"%s\">%s</a>";

    private final static String APPROVE_USER_SUBJECT = "Вас було підтверджено в Системі!";
    private final static String APPROVE_USER_BODY = "Вас було підтверджено в Системі. " +
            "Перейдіть за посиланням, щоб увійти: <a href=\"%s\">%s</a>";

    private final static String REJECT_USER_SUBJECT = "Вас було відхилено Системою!";
    private final static String REJECT_USER_BODY = "Вас було відхилено Системою. " +
            "Зверніться до адміністратора, щоб отримати деталі!";

    private final static String ACTIVATE_USER_SUBJECT = "Вас було активовано в Системі!";
    private final static String ACTIVATE_USER_BODY = "Вас було активовано в Системі. " +
            "Перейдіть за посиланням, щоб увійти: <a href=\"%s\">%s</a>";

    private final static String DEACTIVATE_USER_SUBJECT = "Вас було деактивовано в Системі!";
    private final static String DEACTIVATE_USER_BODY = "Вас було деактивовано в Системі " +
            "верніться до адміністратора, щоб отримати деталі!";

    @Override
    public void forgotPassword(String email, String token) {
        String forgotPasswordUrl = frontendUrl + "/auth/forgotpassword/" + token;
        String body = String.format(FORGOT_PASSWORD_BODY, forgotPasswordUrl, forgotPasswordUrl);

        String subject = FORGOT_PASSWORD_SUBJECT;
        sendEmail(email, subject, body);
    }

    @Override
    public void signUpWithCode(String email, String code) {
        String signupInvite = frontendUrl + "/auth/signupbyinvitecode/" + code;
        String body = String.format(SIGNUP_INVITE_BODY, signupInvite, signupInvite);

        String subject = SIGNUP_INVITE_SUBJECT;
        sendEmail(email, subject, body);
    }

    @Override
    public void approveUser(String email) {
        String loginUrl = frontendUrl + "/auth/signin";
        String body = String.format(APPROVE_USER_BODY, loginUrl, loginUrl);

        String subject = APPROVE_USER_SUBJECT;
        sendEmail(email, subject, body);
    }

    @Override
    public void rejectUser(String email) {
        String subject = REJECT_USER_SUBJECT;
        sendEmail(email, subject, REJECT_USER_BODY);
    }

    @Override
    public void activateUser(String email) {
        String loginUrl = frontendUrl + "/auth/signin";
        String body = String.format(ACTIVATE_USER_BODY, loginUrl, loginUrl);

        String subject = ACTIVATE_USER_SUBJECT;
        sendEmail(email, subject, body);
    }

    @Override
    public void deactivateUser(String email) {
        String subject = DEACTIVATE_USER_SUBJECT;
        sendEmail(email, subject, DEACTIVATE_USER_BODY);
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
