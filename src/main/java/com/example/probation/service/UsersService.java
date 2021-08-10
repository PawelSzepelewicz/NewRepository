package com.example.probation.service;

import com.example.probation.core.entity.User;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    User registerNewUser(final User newUser);

    void redefineRating(final User winner, final User loser);

    List<User> getUsersForComparison();

    List<User> getTopUsersByRating();

    Integer calculateWinnerRating(Integer currentRating);

    Integer calculateLoserRating(Integer currentRating);

    Optional<User> findByUserName(String username);

    Optional<User> getUserByToken(String token);

    User saveRegisteredUser(String token);

    User getCurrentUser();

    boolean checkEmailExistence(String email);

    boolean checkUsernameExistence(String email);
}
