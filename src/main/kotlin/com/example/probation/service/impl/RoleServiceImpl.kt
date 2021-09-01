package com.example.probation.service.impl

import com.example.probation.exception.EntityNotFoundException
import com.example.probation.repository.RoleRepository
import com.example.probation.service.RoleService
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl(private val roleRepository: RoleRepository) : RoleService {

    override fun getRoleByRoleName(roleName: String) =
        roleRepository.getRoleByRoleName(roleName) ?: throw EntityNotFoundException("{entity.notfound}")
}
