package com.example.probation.service

import com.example.probation.core.entity.Role

interface RoleService {
    fun getRoleByRoleName(roleName: String): Role?
}