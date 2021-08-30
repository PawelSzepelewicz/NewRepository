package com.example.probation.configurations

import com.example.probation.service.impl.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val userDetailsService: CustomUserDetailsService
) : WebSecurityConfigurerAdapter() {
    @Bean
    protected fun passwordEncoder(): PasswordEncoder {
        val power = 12
        return BCryptPasswordEncoder(power)
    }

    override fun configure(http: HttpSecurity) {
        val homeUrl = "/home"
        val adminRole = "ADMIN"
        val userRole = "USER"
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/accounts/**").permitAll()
            .antMatchers("/users/password").permitAll()
            .antMatchers("/users/update").permitAll()
            .antMatchers("/registration").permitAll()
            .antMatchers("/block/{userId}").permitAll()
            .antMatchers(homeUrl).permitAll()
            .antMatchers("/users/{winnerId}/win/{loserId}").hasAnyAuthority(adminRole, userRole)
            .antMatchers("/users/top").permitAll()
            .antMatchers("/users/random").permitAll()
            .antMatchers("/confirmation/**").permitAll()
            .antMatchers("/**").permitAll()
        http.csrf().disable()
            .formLogin().defaultSuccessUrl(homeUrl)
            .and().logout()
        http.logout()
            .logoutSuccessHandler { _: HttpServletRequest, response: HttpServletResponse, _: Authentication ->
                response.sendRedirect(
                    homeUrl
                )
            }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }
}
