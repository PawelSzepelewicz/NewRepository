package com.example.probation.service;

import com.example.probation.core.entity.Role;
import com.example.probation.core.entity.User;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {
    @Mock
    private UsersRepository usersRepository;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    @Mock
    private RoleService roleService;
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

    @Test
    void registerNewUser() {
        String defaultPassword = "Pasha2000";
        User testUser = new User();
        testUser.setUsername("Pawel");
        testUser.setDescription("Description");
        testUser.setEmail("email@outlook.com");
        testUser.setPassword(defaultPassword);
        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setRole("USER");
        Mockito.when(usersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(roleService.getRoleByRole(any())).thenAnswer(i -> testRole);
        User createdUser = service.registerNewUser(testUser);
        assertTrue(passwordEncoder.matches(defaultPassword, createdUser.getPassword()));
        assertTrue(createdUser.getRoles().contains(testRole));
    }
}
