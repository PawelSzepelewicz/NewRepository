package com.example.probation.event;

import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final UsersService service;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    public void confirmRegistration(OnRegistrationCompleteEvent event) {
        var user = event.getUser();
        var token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        var subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/confirmation?token=" + token;
        String message = messages.getMessage("message.registrationSuccess", null, event.getLocale());
        var email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }
}
