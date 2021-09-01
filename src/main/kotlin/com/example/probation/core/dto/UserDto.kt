package com.example.probation.core.dto

data class UserDto(
    var id: Long? = null,
    var username: String? = null,
    var description: String? = null,
    var rating: Int? = null,
    var email: String? = null,
    var roles: Set<RoleDto> = emptySet()
)
