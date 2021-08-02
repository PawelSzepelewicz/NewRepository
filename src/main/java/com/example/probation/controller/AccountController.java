package com.example.probation.controller;

import com.example.probation.event.OnRegistrationCompleteEvent;
import com.example.probation.model.CreateUserDto;
import com.example.probation.model.User;
import com.example.probation.model.UserDto;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final MapperFacade mapper;
    private final UsersService service;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid final CreateUserDto newUser, HttpServletRequest request) {
        final var user = mapper.map(newUser, User.class);
        var registeredUser = service.registerNewUser(user);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(request.getContextPath(),
                request.getLocale(), registeredUser));

        return ResponseEntity.ok(mapper.map(registeredUser, UserDto.class));
    }

    @GetMapping
    public ResponseEntity<UserDto> confirmRegistration(@RequestParam("token") String token) {
        return ResponseEntity.ok(mapper.map(service.saveRegisteredUser(token), UserDto.class));
    }
}
