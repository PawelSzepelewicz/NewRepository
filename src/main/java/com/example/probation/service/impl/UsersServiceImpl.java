package com.example.probation.service.impl;

import com.example.probation.model.Role;
import com.example.probation.model.User;
import com.example.probation.repository.RoleRepository;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User saveNewUser(final User user) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.getRoleById(1L));
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    @Override
    public void redefineRating(final User winner, final User loser) {
        loser.setRating(calculateLoserRating(loser.getRating()));
        winner.setRating(calculateWinnerRating(winner.getRating()));
        usersRepository.save(winner);
        usersRepository.save(loser);
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
    public User findByUserName(String username) {
        return usersRepository.findByUsername(username);
    }
}
