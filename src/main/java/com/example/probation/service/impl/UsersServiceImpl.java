package com.example.probation.service.impl;

import com.example.probation.model.User;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    @Override
    public User saveNewUser(final User user) {
        return usersRepository.save(user);
    }

    @Override
    public void redefineRating(final User winner, final User loser) {
        loser.setRating(loser.getRating() - 15);
        winner.setRating(winner.getRating() + 15);
        usersRepository.save(winner);
        usersRepository.save(loser);
    }

    @Override
    public List<User> getUsersForComparison() {
        return usersRepository.getRandomUsers();
    }

    @Override
    public List<User> getTopUsersByRating() {
        return usersRepository.findAllByRating();
    }
}
