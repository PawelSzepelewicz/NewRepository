package com.example.probation.repository;

import com.example.probation.core.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/init.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersRepositoryTest {
    @Autowired
    private UsersRepository repository;
    @Autowired
    private RoleRepository rolesRepository;

    @Test
    void getById() throws Exception {
        final Optional<User> userA = repository.findById(1L);
        final Optional<User> userB = repository.findById(2L);
        assertTrue(userA.isPresent());
        assertTrue(userB.isPresent());
        assertTrue(userA.get().getUsername().contains("Admin"));
        assertTrue(userB.get().getUsername().contains("User"));
    }

    @Test
    void getRandomUsers() {
        final var random = repository.getRandomUsers();
        assertNotEquals(random.get(0), random.get(1));
        assertEquals(2, random.size());
    }

    @Test
    void findAllByOrderByRatingDesc() {
        final List<User> userList = repository.findAllByOrderByRatingDesc();
        final List<Integer> ratings = new ArrayList<>();
        userList.forEach(user -> ratings.add(user.getRating()));
        assertEquals(ratings.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()), ratings);
    }

    @Test
    void findByUsername() {
        final String username = "Next";
        assertEquals(repository.findByUsername(username).get().getUsername(), username);
    }

    @Test
    void findByEmail() {
        final String email = "next@gmail.com";
        assertEquals(repository.findByEmail(email).get().getEmail(), email);
    }
}
