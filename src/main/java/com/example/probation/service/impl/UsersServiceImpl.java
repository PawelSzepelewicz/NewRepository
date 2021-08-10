package com.example.probation.service.impl;

import com.example.probation.exception.*;
import com.example.probation.core.entity.Role;
import com.example.probation.core.entity.User;
import com.example.probation.core.entity.VerificationToken;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.RoleService;
import com.example.probation.service.TokenService;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final TokenService tokenService;
    private final CustomUserDetailsService detailsService;

    @Override
    public User registerNewUser(final User user) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.getRoleByRole("USER"));
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            return usersRepository.save(user);
    }

    @Override
    public boolean checkEmailExistence(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean checkUsernameExistence(String username) {
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
    public Integer calculateWinnerRating(Integer currentRating) {
        return currentRating + 15;
    }

    @Override
    public Integer calculateLoserRating(Integer currentRating) {
        return currentRating - 15;
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public User getCurrentUser() {
        String username = detailsService.getCurrentUsername();

        if (username == null) {
            throw new NoSuchUserException();
        }

        return findByUserName(username).orElseThrow(ForbiddenException::new);
    }

    @Override
    public Optional<User> getUserByToken(final String token) {
        return Optional.of(tokenService.findByToken(token).orElseThrow(TokenNotFoundException::new).getUser());
    }

    @Override
    public User saveRegisteredUser(final String token) {
        var verificationToken = tokenService.findByToken(token).orElseThrow(UserNotFoundByTokenException::new);
        var user = tokenService.getUserByToken(token).orElseThrow();
        var cal = Calendar.getInstance();

        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new TimeHasExpiredException();
        }

        user.setId(user.getId());
        user.setEnabled(true);
        usersRepository.save(user);

        return user;
    }
}
