package com.example.probation.controller;

import com.example.probation.model.*;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping("/{winnerId}/win/{loserId}")
    public ResponseEntity<SuccessMessage> changeRating(@PathVariable("winnerId") final User winner, @PathVariable("loserId") final User loser) {
        service.redefineRating(winner, loser);

        return ResponseEntity.ok(new SuccessMessage());
    }

    @GetMapping("/random")
    public ResponseEntity<List<SelectedUsersDto>> getUsersForComparison() {
        return ResponseEntity.ok(mapper.mapAsList(service.getUsersForComparison(), SelectedUsersDto.class));
    }

    @GetMapping("/getByRating")
    public ResponseEntity<List<SelectedUsersDto>> getTopByRating() {
        return ResponseEntity.ok(mapper.mapAsList(service.getTopUsersByRating(), SelectedUsersDto.class));
    }
}
