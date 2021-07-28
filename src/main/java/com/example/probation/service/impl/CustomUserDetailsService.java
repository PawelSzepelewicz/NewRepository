package com.example.probation.service.impl;

import com.example.probation.model.User;
import com.example.probation.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = usersRepository.findByUsername(username);

        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public String getCurrentUsername() throws NullPointerException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.equals(null)) {
            throw new NullPointerException();
        } else if (auth.getName().equals("anonymousUser")) {
            return null;
        }

        return auth.getName();
    }
}
