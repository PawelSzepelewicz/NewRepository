package com.example.probation.service.impl

import com.example.probation.core.dto.UserDetailsDto
import com.example.probation.event.OnLoggingCompleteEvent
import com.example.probation.exception.ForbiddenException
import com.example.probation.exception.UserNotFoundException
import com.example.probation.repository.UsersRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class CustomUserDetailsService(
    private val usersRepository: UsersRepository,
    private val eventPublisher: ApplicationEventPublisher
) : UserDetailsService {
    companion object {
        const val ANONYMOUS = "anonymousUser"
    }

    override fun loadUserByUsername(username: String): UserDetails =
        usersRepository.findByUsername(username)?.let {
            UserDetailsDto(
                it.id,
                it.username,
                it.password,
                it.roles,
                it.enabled
            ).also { userDto ->
                eventPublisher.publishEvent(OnLoggingCompleteEvent(userDto))
            }
        } ?: throw UserNotFoundException("No such user, $username")

    fun getCurrentUsername(): String =
        SecurityContextHolder.getContext().authentication?.run {
            if (ANONYMOUS == name) {
                throw ForbiddenException("{forbidden}")
            }
            name
        } ?: throw ForbiddenException("{forbidden}")
}
