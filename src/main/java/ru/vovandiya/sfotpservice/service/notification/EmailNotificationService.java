package ru.vovandiya.sfotpservice.service.notification;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service("emailNotification")
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final String username;
    private final String password;
    private final String fromEmail;
    private final Session session;

    public EmailNotificationService() {
        var config = loadConfig();
        username = config.getProperty("email.username");
        password = config.getProperty("email.password");
        fromEmail = config.getProperty("email.from");
        session = Session.getInstance(config, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    @Override
    public void send(String destination, String code) {
        try {
            var message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(destination));
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + code);
            Transport.send(message);
            log.info("OTP sent via email to {}", destination);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", destination, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private Properties loadConfig() {
        try {
            var props = new Properties();
            props.load(EmailNotificationService.class
                .getClassLoader().getResourceAsStream("email.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email configuration", e);
        }
    }
}