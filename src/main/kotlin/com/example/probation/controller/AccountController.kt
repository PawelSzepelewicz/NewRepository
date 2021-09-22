package com.example.probation.controller

import com.example.probation.core.dto.CreateUserDto
import com.example.probation.core.dto.SuccessMessage
import com.example.probation.core.dto.UserDto
import com.example.probation.core.entity.User
import com.example.probation.service.UsersService
import ma.glasnost.orika.MapperFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val mapper: MapperFacade,
    private val service: UsersService
) {
    @PostMapping
    fun registerUser(@RequestBody @Valid newUser: CreateUserDto) =
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
    fun confirmRegistration(@RequestParam("token") token: String) =
        ResponseEntity.ok(
            mapper.map(
                service.saveRegisteredUser(token),
                UserDto::class.java
            )
        )

    @PostMapping("/block/{userId}")
    fun blockUser(@PathVariable("userId") userId: Long) =
        service.blockUser(userId).let {
            ResponseEntity.ok(SuccessMessage())
        }

    @PostMapping("/unblock/{userId}")
    fun unblockUser(@PathVariable("userId") userId: Long) =
        service.unblockUser(userId).let {
            ResponseEntity.ok(SuccessMessage())
        }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable("userId") userId: Long) =
        service.deleteUser(userId).let {
            ResponseEntity.ok(SuccessMessage())
        }
}
