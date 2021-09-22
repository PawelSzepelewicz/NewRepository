package com.example.probation.core.dto

import com.example.probation.core.entity.Role

data class UserDto(
    var id: Long? = null,
    var username: String? = null,
    var description: String? = null,
    var rating: Int? = null,
    var email: String? = null,
    var roles: MutableSet<Role> = mutableSetOf()
)
