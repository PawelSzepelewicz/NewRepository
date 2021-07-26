package com.example.probation.controller;

import com.example.probation.exception.ForbiddenException;
import com.example.probation.model.SelectedUsersDto;
import com.example.probation.model.SuccessMessage;
import com.example.probation.model.User;
import com.example.probation.model.UserDto;
import com.example.probation.service.UsersService;
import com.example.probation.service.impl.CustomUserDetailsService;
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
    private final CustomUserDetailsService userDetailsService;

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

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser() throws ForbiddenException {
        String username = userDetailsService.getCurrentUsername();
        if(username == null) {
            throw new ForbiddenException();
        }

        return ResponseEntity.ok(mapper.map(service.findByUserName(username), UserDto.class));
    }
}
