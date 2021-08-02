package com.example.probation.service;

import com.example.probation.model.User;
import com.example.probation.model.VerificationToken;

import java.util.Optional;

public interface TokenService {
    void saveNewToken(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    Optional<User> getUserByToken(String token);
}
