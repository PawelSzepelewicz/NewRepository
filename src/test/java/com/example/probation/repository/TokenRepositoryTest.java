package com.example.probation.repository;

import com.example.probation.core.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Sql({"/token.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenRepositoryTest {
    @Autowired
    TokenRepository repository;
    @Autowired
    UsersRepository usersRepository;

    @Test
    void findByToken() {
        String wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een";
        String token = "902a8cf0-63a7-444f-a424-8960907e7eeb";
        assertTrue(repository.findByToken(token).isPresent());
        assertEquals(repository.findByToken(token).get().getToken(), token);
        assertFalse(repository.findByToken(wrongToken).isPresent());
    }

    @Test
    void getByUser() {
        User user = usersRepository.findByUsername("Admin").get();
        String token = "902a8cf0-63a7-444f-a424-8960907e7eeb";
        assertTrue(repository.getByUser(user).isPresent());
        assertEquals(repository.getByUser(user).get().getToken(), token);
    }
}
