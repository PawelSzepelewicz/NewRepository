package com.example.probation.service.impl;

import com.example.probation.exception.ForbiddenException;
import com.example.probation.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName().equals("anonymousUser")) {
            throw new ForbiddenException();
        }

        return auth.getName();
    }
}
