package com.example.probation.service.impl;

import com.example.probation.model.User;
import com.example.probation.repository.UsersRepository;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    @Override
    public User saveNewUser(User user) {
        usersRepository.save(user);

        return user;
    }
}
