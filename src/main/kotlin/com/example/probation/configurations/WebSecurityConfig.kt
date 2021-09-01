package com.example.probation.configurations

import com.example.probation.core.enums.Roles
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
open class WebSecurityConfig(
    private val userDetailsService: CustomUserDetailsService
) : WebSecurityConfigurerAdapter() {
    companion object {
        const val POWER = 12
    }

    @Bean
    protected open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(POWER)

    override fun configure(http: HttpSecurity) {
        val homeUrl = "/home"
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/accounts/**").permitAll()
            .antMatchers("/users/password").permitAll()
            .antMatchers("/users/update").permitAll()
            .antMatchers("/registration").hasAnyAuthority(Roles.ADMIN.role)
            .antMatchers("/block/**").hasAnyAuthority(Roles.ADMIN.role)
            .antMatchers("/unblock/**").hasAnyAuthority(Roles.ADMIN.role)
            .antMatchers("/delete/**").hasAnyAuthority(Roles.ADMIN.role)
            .antMatchers(homeUrl).permitAll()
            .antMatchers("/users/{winnerId}/win/{loserId}").hasAnyAuthority(Roles.ADMIN.role, Roles.USER.role)
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
