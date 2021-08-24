package com.example.probation.controller;

import com.example.probation.core.dto.CreateUserDto;
import com.example.probation.core.dto.UserDto;
import com.example.probation.core.entity.User;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.Valid;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final MapperFacade mapper;
    private final UsersService service;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid final CreateUserDto newUser) {
        final var user = mapper.map(newUser, User.class);

        return ResponseEntity.ok(mapper.map(service.registerNewUser(user), UserDto.class));
    }

    @GetMapping
    public ResponseEntity<UserDto> confirmRegistration(@RequestParam("token") final String token) {
        return ResponseEntity.ok(mapper.map(service.saveRegisteredUser(token), UserDto.class));
    }
}
