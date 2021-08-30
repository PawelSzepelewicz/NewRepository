package com.example.probation.service.impl

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.dto.UserDetailsDto
import com.example.probation.exception.ForbiddenException
import com.example.probation.exception.NoSuchUserException
import com.example.probation.repository.UsersRepository

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class CustomUserDetailsService(
    private val usersRepository: UsersRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return usersRepository.findByUsername(username).let {
            it ?: throw NoSuchUserException(String.format("No such user, %s", username))
            UserDetailsDto(
                it.id,
                it.username!!,
                it.password!!,
                it.roles,
                it.enabled
            )
        }
    }

    fun getCurrentUsername(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        val anonymous = "anonymousUser"
        if (auth == null || anonymous == auth.name) {
            throw ForbiddenException("{forbidden}")
        }
        return auth.name
    }
}
