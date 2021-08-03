package com.example.probation.service.impl;

import com.example.probation.model.User;
import com.example.probation.model.VerificationToken;
import com.example.probation.repository.TokenRepository;
import com.example.probation.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

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
}
