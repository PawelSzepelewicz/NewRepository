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
        return Set.of(new Role("USER"));
    }

    @Test
    void calculateWinnerRating() {
        final Integer winnerRating = service.calculateWinnerRating(1000);
        assertEquals(winnerRating, 1015);
    }

    @Test
    void calculateLoserRating() {
        final Integer loserRating = service.calculateLoserRating(1000);
        assertEquals(loserRating, 985);
    }

    @Test
    void registerNewUser() {
        final String defaultPassword = "Pasha2000";
        final User testUser = new User();
        testUser.setUsername("Pawel");
        testUser.setDescription("Description");
        testUser.setEmail("email@outlook.com");
        testUser.setPassword(defaultPassword);
        final Role testRole = new Role();
        testRole.setId(1L);
        testRole.setRole("USER");
        Mockito.when(usersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(roleService.getRoleByRole(any())).thenAnswer(i -> testRole);
        final User createdUser = service.registerNewUser(testUser);
        assertTrue(passwordEncoder.matches(defaultPassword, createdUser.getPassword()));
        assertTrue(createdUser.getRoles().contains(testRole));
    }

    @Test
    void getTopUsersByRating() {
        final User userA = new User("Higher", "Description", "higher@gmail.com",
                3000, "1@higher", getUsersRoles(), true);
        final User userB = new User("Lower", "Description", "lower@gmail.com",
                2000, "2@lower", getUsersRoles(), true);
        final List<User> top = new ArrayList<>();
        top.add(userA);
        top.add(userB);
        Mockito.when(usersRepository.findAllByOrderByRatingDesc()).thenAnswer(i -> top);
        final List<User> usersTop = service.getTopUsersByRating();
        assertTrue(usersTop.get(0).getRating() >= usersTop.get(1).getRating());
    }

    @Test
    void saveRegisteredUser() {
        final User user = new User("Higher", "Description", "higher@gmail.com",
                3000, "1@higher", getUsersRoles(), false);
        final String token = "902a8cf0-63a7-444f-a424-8960907e7een";
        final String wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een";
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(token, user, calendar.getTime()));
        Mockito.when(tokenService.findByToken(token)).thenReturn(verificationToken);
        Mockito.when(tokenService.getUserByToken(token)).thenReturn(Optional.of(user));
        Mockito.when(usersRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        final User registeredUser = service.saveRegisteredUser(token);
        assertTrue(registeredUser.isEnabled());
        assertThrows(UserNotFoundByTokenException.class, () -> service.saveRegisteredUser(wrongToken));
    }

    @Test
    void getUserByToken() {
        final String token = "902a8cf0-63a7-444f-a424-8960907e7een";
        final String wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een";
        final User user = new User("Simple", "Description", "simple@gmail.com",
                3000, "1@higher", getUsersRoles(), false);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, +2);
        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(token, user, calendar.getTime()));
        Mockito.when(tokenService.findByToken(token)).thenReturn(verificationToken);
        assertTrue(service.getUserByToken(token).isPresent());
        assertSame(service.getUserByToken(token).get(), user);
        assertThrows(TokenNotFoundException.class, () -> service.getUserByToken(wrongToken));
    }

    @Test
    void getCurrentUser() {
        final Optional<User> user = Optional.of(new User("Current", "Description",
                "current@gmail.com", 3000, "1@current", getUsersRoles(), true));
        Mockito.when(detailsService.getCurrentUsername()).thenReturn("Current");
        final User principal = user.get();
        Mockito.when(usersRepository.findByUsername("Current")).thenReturn(user);
        assertEquals(service.getCurrentUser(), user.get());
        assertTrue(service.getCurrentUser().isEnabled());
        assertEquals(service.getCurrentUser().getAuthorities(), getUsersRoles());
        final String wrongName = "Wrong";
        Mockito.when(detailsService.getCurrentUsername()).thenReturn(wrongName);
        assertThrows(ForbiddenException.class, () -> service.getCurrentUser());
        final String nullName = null;
        Mockito.when(detailsService.getCurrentUsername()).thenReturn(nullName);
        assertThrows(NoSuchUserException.class, () -> service.getCurrentUser());
    }

    @Test
    void findByUsername() {
        final Optional<User> user = Optional.of(new User("User", "Description",
                "user@gmail.com", 3000, "1@cuser", getUsersRoles(), true));
        Mockito.when(usersRepository.findByUsername(any())).thenReturn(user);
        final Optional<User> person = service.findByUserName("User");
        assertTrue(person.isPresent());
    }

    @Test
    void getUsersForComparison() {
        final Optional<User> userA = Optional.of(new User("UserA", "1234",
                "a@gmail.com", 3000, "1@cuser", getUsersRoles(), true));
        final Optional<User> userB = Optional.of(new User("UserB", "1234",
                "a@gmail.com", 2500, "2@cuser", getUsersRoles(), true));
        final List<User> random = new ArrayList<>();
        random.add(userA.get());
        random.add(userB.get());
        Mockito.when(usersRepository.getRandomUsers()).thenReturn(random);
        final List<User> userList = service.getUsersForComparison();
        assertNotEquals(userList.get(0).getUsername(), userList.get(1).getUsername());
        assertEquals(2, userList.size());
    }

    @Test
    void redefineRating() {
        final User loser = new User("UserA", "1234", "a@gmail.com", 3000,
                "1@cuser", getUsersRoles(), true);
        final User winner = new User("UserB", "1234", "a@gmail.com", 2500,
                "2@cuser", getUsersRoles(), true);
        final List<User> usersList = new ArrayList<>();
        usersList.add(0, winner);
        usersList.add(1, loser);
        Mockito.when(usersRepository.saveAll(usersList)).thenReturn(usersList);
        service.redefineRating(winner, loser);
        assertTrue(winner.getRating() == 2515 && loser.getRating() == 2985);
    }

    @Test
    void checkEmailExistence() {
        final User user = new User("UserByEmail", "1234", "email@gmail.com",
                3000, "1@cuser", getUsersRoles(), true);
        final String email = "email@gmail.com";
        final String nonexistentEmail = "nonEx@gmail.com";
        Mockito.when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertTrue(service.checkEmailExistence(email));
        assertFalse(service.checkEmailExistence(nonexistentEmail));
    }

    @Test
    void checkUsernameExistence() {
        final User user = new User("UserByName", "1234", "email@gmail.com",
                3000, "1@cuser", getUsersRoles(), true);
        final String name = "UserByName";
        final String nonexistentName = "nonEx";
        Mockito.when(usersRepository.findByUsername(name)).thenReturn(Optional.of(user));
        assertTrue(service.checkUsernameExistence(name));
        assertFalse(service.checkUsernameExistence(nonexistentName));
    }
}
