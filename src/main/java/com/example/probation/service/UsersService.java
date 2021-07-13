package com.example.probation.service;

import com.example.probation.model.User;

import java.util.List;

public interface UsersService {
    User saveNewUser(final User newUser);

    void redefineRating(User winner, User loser);

    List<User> getUsersForComparison();
}
