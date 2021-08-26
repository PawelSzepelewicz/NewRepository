package com.example.probation.controller

import com.example.probation.core.dto.CreateUserDto
import com.example.probation.core.dto.UserDto
import com.example.probation.core.entity.User
import com.example.probation.service.UsersService
import ma.glasnost.orika.MapperFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val mapper: MapperFacade,
    private val service: UsersService
) {
    @PostMapping
    fun registerUser(@RequestBody @Valid newUser: CreateUserDto): ResponseEntity<UserDto> =
        mapper.map(
            newUser,
            User::class.java
        ).let {
            ResponseEntity.ok(
                mapper.map(
                    service.registerNewUser(it),
                    UserDto::class.java
                )
            )
        }

    @GetMapping
    fun confirmRegistration(@RequestParam("token") token: String): ResponseEntity<UserDto> =
        ResponseEntity.ok(
            mapper.map(
                service.saveRegisteredUser(token),
                UserDto::class.java
            )
        )
}
