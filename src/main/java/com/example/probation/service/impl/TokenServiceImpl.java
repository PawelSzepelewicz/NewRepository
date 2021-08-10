package com.example.probation.service.impl;

import com.example.probation.core.entity.User;
import com.example.probation.core.entity.VerificationToken;
import com.example.probation.event.OnRegistrationCompleteEvent;
import com.example.probation.repository.TokenRepository;
import com.example.probation.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    @Override
    public void saveNewToken(VerificationToken token) {
        tokenRepository.save(token);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return Optional.of(findByToken(token).orElseThrow().getUser());
    }

    @Override
    public void confirmRegistration(OnRegistrationCompleteEvent event) {
        var user = event.getUser();
        var token = UUID.randomUUID().toString();
        saveNewToken(new VerificationToken(token, user));
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
