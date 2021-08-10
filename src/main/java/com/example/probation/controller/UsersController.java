package com.example.probation.controller;

import com.example.probation.core.dto.SelectedUsersDto;
import com.example.probation.core.dto.SuccessMessage;
import com.example.probation.core.dto.UserDto;
import com.example.probation.core.entity.User;
import com.example.probation.service.UsersService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final MapperFacade mapper;
    private final UsersService service;

    @PostMapping("/{winnerId}/win/{loserId}")
    public ResponseEntity<SuccessMessage> changeRating(@PathVariable("winnerId") final User winner, @PathVariable("loserId") final User loser) {
        service.redefineRating(winner, loser);

        return ResponseEntity.ok(new SuccessMessage());
    }

    @GetMapping("/random")
    public ResponseEntity<List<SelectedUsersDto>> getUsersForComparison() {
        return ResponseEntity.ok(mapper.mapAsList(service.getUsersForComparison(), SelectedUsersDto.class));
    }

    @GetMapping("/top")
    public ResponseEntity<List<SelectedUsersDto>> getTopByRating() {
        return ResponseEntity.ok(mapper.mapAsList(service.getTopUsersByRating(), SelectedUsersDto.class));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(mapper.map(service.getCurrentUser(), UserDto.class));
    }
}
