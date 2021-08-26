package com.example.probation.controller

import com.example.probation.core.dto.SelectedUserDto
import com.example.probation.core.dto.SuccessMessage
import com.example.probation.core.dto.UserDto
import com.example.probation.core.entity.User
import com.example.probation.service.UsersService

import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UsersController(private val mapper: MapperFacade, private val service: UsersService) {

    @PostMapping("/{winnerId}/win/{loserId}")
    fun changeRating(
        @PathVariable("winnerId") winner: User,
        @PathVariable("loserId") loser: User
    ): ResponseEntity<SuccessMessage> =
        service.redefineRating(winner, loser).let {
            ResponseEntity.ok(SuccessMessage())
        }

    @GetMapping("/random")
    fun getUsersForComparison(): ResponseEntity<List<SelectedUserDto>> =
        ResponseEntity.ok(
            mapper.mapAsList(
                service.getUsersForComparison(),
                SelectedUserDto::class.java
            )
        )

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/top")
    fun getTopByRating(): ResponseEntity<List<SelectedUserDto>> =
        ResponseEntity.ok(
            mapper.mapAsList(
                service.getTopUsersByRating(),
                SelectedUserDto::class.java
            )
        )

    @GetMapping("/current")
    fun getCurrentUser(): ResponseEntity<UserDto> =
        ResponseEntity.ok(
            mapper.map(
                service.getCurrentUser(),
                UserDto::class.java
            )
        )
}
