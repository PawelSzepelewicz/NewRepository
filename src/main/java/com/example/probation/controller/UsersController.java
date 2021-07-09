package com.example.probation.controller;

import com.example.probation.model.CreateUserDto;
import com.example.probation.model.User;
import com.example.probation.model.UserDto;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final MapperFacade mapper;
    private final UsersService service;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid final CreateUserDto newUser) {
        final var user = mapper.map(newUser, User.class);

        return ResponseEntity.ok(mapper.map(service.saveNewUser(user), UserDto.class));
    }
}
