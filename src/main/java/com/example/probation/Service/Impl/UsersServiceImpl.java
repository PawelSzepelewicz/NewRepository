package com.example.probation.Service.Impl;

import com.example.probation.Model.Users;
import com.example.probation.Model.UsersUi;
import com.example.probation.Repository.UsersRepository;
import com.example.probation.Service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    UsersRepository usersRepository;

    @Override
    public Users saveNewUser(UsersUi user) {
        Users newUser = new Users();
        newUser.setUserName(user.getName());
        newUser.setDescription(user.getDescription());
        newUser.setRating(2500);
        return usersRepository.save(newUser);
    }

}
