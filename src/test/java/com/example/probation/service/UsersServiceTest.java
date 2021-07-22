package com.example.probation.service;

import com.example.probation.repository.UsersRepository;
import com.example.probation.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UsersServiceImpl service;

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
