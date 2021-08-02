package com.example.probation.service.impl;

import com.example.probation.model.Role;
import com.example.probation.repository.RoleRepository;
import com.example.probation.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    public final RoleRepository roleRepository;

    @Override
    public Role getRoleByRole(String role) {
        return roleRepository.getRoleByRole(role);
    }
}
