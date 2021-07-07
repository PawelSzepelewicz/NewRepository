package com.example.probation.Controller;

import com.example.probation.Model.UsersUi;
import com.example.probation.Service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/create")
@AllArgsConstructor


public class UsersController {

    @Autowired
    private final UsersService service;

    @PostMapping("/newUser")
    public UsersUi createUser(@RequestBody UsersUi newUser) {
        service.saveNewUser(newUser);
        return newUser;
    }

}
