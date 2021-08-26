package com.example.probation.repository

import com.example.probation.core.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun getRoleByRoleName(roleName: String): Role?
}
