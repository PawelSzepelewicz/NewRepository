package com.example.probation.core.dto

import com.example.probation.core.entity.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsDto(
    var id: Long? = null,
    private var username: String,
    private var password: String,
    var roles: Set<Role>? = emptySet(),
    var enabled: Boolean
) : UserDetails {
    override fun getAuthorities(): Set<GrantedAuthority>? = roles

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled
}
