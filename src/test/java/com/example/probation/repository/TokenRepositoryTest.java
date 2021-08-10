package com.example.probation.repository;

import com.example.probation.core.entity.User;
import com.example.probation.core.entity.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/init.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenRepositoryTest {
    @Autowired
    private TokenRepository repository;
    @Autowired
    private UsersRepository usersRepository;

    @Test
    void findByToken() {
        final String wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een";
        final String token = "902a8cf0-63a7-444f-a424-8960907e7eeb";
        final Optional<VerificationToken> verificationToken = repository.findByToken(token);
        assertTrue(verificationToken.isPresent());
        assertEquals(verificationToken.get().getToken(), token);
        assertFalse(repository.findByToken(wrongToken).isPresent());
    }

    @Test
    void getByUser() {
        final User user = usersRepository.findByUsername("Admin").get();
        final String token = "902a8cf0-63a7-444f-a424-8960907e7eeb";
        final Optional<VerificationToken> verificationToken = repository.getByUser(user);
        assertTrue(verificationToken.isPresent());
        assertEquals(verificationToken.get().getToken(), token);
    }
}
