package com.example.probation.service;

import com.example.probation.core.entity.Role;
import com.example.probation.core.entity.User;
import com.example.probation.core.entity.VerificationToken;
import com.example.probation.exception.ForbiddenException;
import com.example.probation.exception.NoSuchUserException;
import com.example.probation.exception.TokenNotFoundException;
import com.example.probation.exception.UserNotFoundByTokenException;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.impl.CustomUserDetailsService;
import com.example.probation.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

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
    @Mock
    private TokenService tokenService;
    @Mock
    private CustomUserDetailsService detailsService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private UsersServiceImpl service;

    public Set<Role> getUsersRoles() {
        Set<Role> roles = new HashSet<>();
        Role role = new Role("USER");
        roles.add(role);
        return roles;
    }

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

    @Test
    void getTopUsersByRating() {
        User userA = new User("Higher", "Description", "higher@gmail.com",
                3000, "1@higher", getUsersRoles(), true);
        User userB = new User("Lower", "Description", "lower@gmail.com",
                2000, "2@lower", getUsersRoles(), true);
        List<User> top = new ArrayList<>();
        top.add(userA);
        top.add(userB);
        Mockito.when(usersRepository.findAllByOrderByRatingDesc()).thenAnswer(i -> top);
        List<User> usersTop = service.getTopUsersByRating();
        assertTrue(usersTop.get(0).getRating() >= usersTop.get(1).getRating());
    }

    @Test
    void saveRegisteredUser() {
        User user = new User("Higher", "Description", "higher@gmail.com",
                3000, "1@higher", getUsersRoles(), false);
        String token = "902a8cf0-63a7-444f-a424-8960907e7een";
        String wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, +2);
        Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(token, user, calendar.getTime()));
        Mockito.when(tokenService.findByToken(token)).thenReturn(verificationToken);
        Mockito.when(tokenService.getUserByToken(token)).thenReturn(Optional.of(user));
        Mockito.when(usersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        User registeredUser = service.saveRegisteredUser(token);
        assertTrue(registeredUser.isEnabled());
        assertThrows(UserNotFoundByTokenException.class, () -> service.saveRegisteredUser(wrongToken));
    }

    @Test
    void getUserByToken() {
        String token = "902a8cf0-63a7-444f-a424-8960907e7een";
        String wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een";
        User user = new User("Simple", "Description", "simple@gmail.com",
                3000, "1@higher", getUsersRoles(), false);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, +2);
        Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(token, user, calendar.getTime()));
        Mockito.when(tokenService.findByToken(token)).thenReturn(verificationToken);
        assertTrue(service.getUserByToken(token).isPresent());
        assertSame(service.getUserByToken(token).get(), user);
        assertThrows(TokenNotFoundException.class, () -> service.getUserByToken(wrongToken));
    }

    @Test
    void getCurrentUser() {
        Optional<User> user = Optional.of(new User("Current", "Description",
                "current@gmail.com", 3000, "1@current", getUsersRoles(), true));
        Mockito.when(detailsService.getCurrentUsername()).thenReturn("Current");
        User principal = user.get();
        Mockito.when(usersRepository.findByUsername("Current")).thenReturn(user);
        assertEquals(service.getCurrentUser(), user.get());
        assertTrue(service.getCurrentUser().isEnabled());
        assertEquals(service.getCurrentUser().getAuthorities(), getUsersRoles());
        String wrongName = "Wrong";
        Mockito.when(detailsService.getCurrentUsername()).thenReturn(wrongName);
        assertThrows(ForbiddenException.class, () -> service.getCurrentUser());
        String nullName = null;
        Mockito.when(detailsService.getCurrentUsername()).thenReturn(nullName);
        assertThrows(NoSuchUserException.class, () -> service.getCurrentUser());
    }

    @Test
    void findByUsername() {
        Optional<User> user = Optional.of(new User("User", "Description",
                "user@gmail.com", 3000, "1@cuser", getUsersRoles(), true));
        Mockito.when(usersRepository.findByUsername(any())).thenReturn(user);
        assertTrue(service.findByUserName("User").isPresent());
    }

    @Test
    void getUsersForComparison() {
        Optional<User> userA = Optional.of(new User("UserA", "1234",
                "a@gmail.com", 3000, "1@cuser", getUsersRoles(), true));
        Optional<User> userB = Optional.of(new User("UserB", "1234",
                "a@gmail.com", 2500, "2@cuser", getUsersRoles(), true));
        List<User> random = new ArrayList<>();
        random.add(userA.get());
        random.add(userB.get());
        Mockito.when(usersRepository.getRandomUsers()).thenReturn(random);
        assertFalse(service.getUsersForComparison().get(0).getUsername().equals(service.getUsersForComparison().get(1).getUsername()));
        assertEquals(2, service.getUsersForComparison().size());
    }

    @Test
    void redefineRating() {
        User loser = new User("UserA", "1234", "a@gmail.com", 3000,
                "1@cuser", getUsersRoles(), true);
        User winner = new User("UserB", "1234", "a@gmail.com", 2500,
                "2@cuser", getUsersRoles(), true);
        List<User> usersList = new ArrayList<>();
        usersList.add(0, winner);
        usersList.add(1, loser);
        Mockito.when(usersRepository.saveAll(usersList)).thenReturn(usersList);
        service.redefineRating(winner, loser);
        assertTrue(winner.getRating() == 2515 && loser.getRating() == 2985);
    }

    @Test
    void checkEmailExistence() {
        User user = new User("UserByEmail", "1234", "email@gmail.com",
                3000, "1@cuser", getUsersRoles(), true);
        String email = "email@gmail.com";
        String nonexistentEmail = "nonEx@gmail.com";
        Mockito.when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertTrue(service.checkEmailExistence(email));
        assertFalse(service.checkEmailExistence(nonexistentEmail));
    }

    @Test
    void checkUsernameExistence() {
        User user = new User("UserByName", "1234", "email@gmail.com",
                3000, "1@cuser", getUsersRoles(), true);
        String name = "email@gmail.com";
        String nonexistentName = "nonEx";
        Mockito.when(usersRepository.findByEmail(name)).thenReturn(Optional.of(user));
        assertTrue(service.checkEmailExistence(name));
        assertFalse(service.checkEmailExistence(nonexistentName));
    }
}
