package com.example.probation.controller

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.core.dto.SelectedUserDto
import com.example.probation.core.dto.SuccessMessage
import com.example.probation.core.dto.UserDto
import com.example.probation.core.entity.User
import com.example.probation.service.UsersService
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

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

    @PostMapping("/password")
    fun changePassword(
        @RequestBody @Valid passwordDto: ChangePasswordDto
    ): ResponseEntity<SuccessMessage> =
        service.changePassword(passwordDto).let {
            ResponseEntity.ok(SuccessMessage())
        }

    @PutMapping("/update/{id}")
    fun changePersonalData(
        @PathVariable("id") id: Long,
        @RequestBody @Valid changedInfo: ChangeInfoDto): ResponseEntity<UserDto> =
        mapper.map(
            changedInfo,
            User::class.java
        ).let {
            ResponseEntity.ok(
                mapper.map(
                    service.changePersonalData(id, it),
                    UserDto::class.java
                )
            )
        }
}
