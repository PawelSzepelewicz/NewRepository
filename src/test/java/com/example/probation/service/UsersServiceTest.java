package com.example.probation.service;

import com.example.probation.repository.UsersRepository;
import com.example.probation.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UsersServiceTest {
    @InjectMocks
    UsersRepository usersRepository;
    @InjectMocks
    PasswordEncoder passwordEncoder;
    UsersService service = new UsersServiceImpl(usersRepository, passwordEncoder);

    @Test
    void calculateWinnerRating() {
        Integer winnerRating = service.calculateWinnerRating(1000);
        assertEquals(winnerRating, 1015);
    }

    @Test
    void calculateLoserRating() {
        Integer loserRating = service.calculateLoserRating(1000);
        assertEquals(loserRating, 985);
    }
}
