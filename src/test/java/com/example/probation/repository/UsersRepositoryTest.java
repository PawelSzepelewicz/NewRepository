package com.example.probation.repository;

import com.example.probation.core.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Sql({"/users-repository.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersRepositoryTest {

    @Autowired
    UsersRepository repository;
    @Autowired
    RoleRepository rolesRepository;

    @Test
    void getById() throws Exception {
        assertTrue(repository.findById(1L).get().getUsername().contains("Admin"));
        assertTrue(repository.findById(2L).get().getUsername().contains("User"));
    }

    @Test
    void getRandomUsers() {
        assertNotEquals(repository.getRandomUsers().get(0), repository.getRandomUsers().get(1));
        assertEquals(2, repository.getRandomUsers().size());
    }

    @Test
    void findAllByOrderByRatingDesc() {
        List<User> userList = repository.findAllByOrderByRatingDesc();

        for (int a = 0; a < userList.size() - 1; a++) {
            assertTrue(userList.get(a).getRating() >= userList.get(a + 1).getRating());
        }
    }

    @Test
    void findByUsername() {
        String username = "Next";
        assertEquals(repository.findByUsername(username).get().getUsername(), username);
    }

    @Test
    void findByEmail() {
        String email = "next@gmail.com";
        assertEquals(repository.findByEmail(email).get().getEmail(), email);
    }
}
