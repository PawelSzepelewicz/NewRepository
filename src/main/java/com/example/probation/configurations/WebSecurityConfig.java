package com.example.probation.configurations;

import com.example.probation.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var homeUrl = "/home";
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/account/**").permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/users/{winnerId}/win/{loserId}").hasAnyAuthority("ADMIN", "USER")
                .antMatchers("/users/getByRating").permitAll()
                .antMatchers("/users/random").permitAll()
                .antMatchers(homeUrl).permitAll()
                .antMatchers("/confirmation/**").permitAll()
                .antMatchers("/**").permitAll()
                .and().formLogin().defaultSuccessUrl(homeUrl).and().logout();
        http.logout().logoutSuccessHandler((request, response, authentication) -> response.sendRedirect(homeUrl));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
