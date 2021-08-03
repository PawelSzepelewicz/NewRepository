package com.example.probation.service;

import com.example.probation.event.OnRegistrationCompleteEvent;
import com.example.probation.core.entity.User;
import com.example.probation.core.entity.VerificationToken;

import java.util.Optional;

public interface TokenService {
    void saveNewToken(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    Optional<User> getUserByToken(String token);

    void confirmRegistration(OnRegistrationCompleteEvent event);
}
