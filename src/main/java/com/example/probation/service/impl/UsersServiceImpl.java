package com.example.probation.service.impl;

import com.example.probation.core.entity.User;
import com.example.probation.event.OnRegistrationCompleteEvent;

import com.example.probation.exception.ForbiddenException;
import com.example.probation.exception.TimeHasExpiredException;
import com.example.probation.exception.NoSuchUserException;
import com.example.probation.exception.TokenNotFoundException;
import com.example.probation.exception.UserNotFoundByTokenException;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.RoleService;
import com.example.probation.service.TokenService;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Calendar;

@Transactional
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    public static final Integer ADDITION = 15;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final TokenService tokenService;
    private final CustomUserDetailsService detailsService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public User registerNewUser(final User user) {
        final var userRole = "USER";
        user.setRoles(Set.of(roleService.getRoleByRoleName(userRole)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return usersRepository.save(user);
    }

    @Override
    public boolean checkEmailExistence(final String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean checkUsernameExistence(final String username) {
        return usersRepository.findByUsername(username).isPresent();
    }

    @Override
    public void redefineRating(final User winner, final User loser) {
        final List<User> players = new ArrayList<>();
        loser.setRating(calculateLoserRating(loser.getRating()));
        winner.setRating(calculateWinnerRating(winner.getRating()));
        players.add(winner);
        players.add(loser);
        usersRepository.saveAll(players);
    }

    @Override
    public List<User> getUsersForComparison() {
        return usersRepository.getRandomUsers();
    }

    @Override
    public List<User> getTopUsersByRating() {
        return usersRepository.findAllByOrderByRatingDesc();
    }

    @Override
    public Integer calculateWinnerRating(final Integer currentRating) {
        return currentRating + ADDITION;
    }

    @Override
    public Integer calculateLoserRating(final Integer currentRating) {
        return currentRating - ADDITION;
    }

    @Override
    public Optional<User> findByUserName(final String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public User getCurrentUser() {
        final String username = detailsService.getCurrentUsername();

        if (username == null) {
            throw new NoSuchUserException("{user.no.such}");
        }

        return findByUserName(username).orElseThrow(ForbiddenException::new);
    }

    @Override
    public Optional<User> getUserByToken(final String token) {
        return Optional.of(tokenService.findByToken(token).orElseThrow(TokenNotFoundException::new).getUser());
    }

    @Override
    public User saveRegisteredUser(final String token) {
        final var verificationToken = tokenService.findByToken(token).orElseThrow(UserNotFoundByTokenException::new);
        final var user = tokenService.getUserByToken(token).orElseThrow();
        final var cal = Calendar.getInstance();

        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new TimeHasExpiredException();
        }

        user.setId(user.getId());
        user.setEnabled(true);
        usersRepository.save(user);

        return user;
    }
}
