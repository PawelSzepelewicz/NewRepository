package com.example.probation.configurations

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.Locale

@Configuration
class MvcConfig : WebMvcConfigurer {
    override fun getValidator(): Validator =
        LocalValidatorFactoryBean().apply {
            setValidationMessageSource(messageSource())
        }

    @Bean
    fun messageSource(): MessageSource =
        ReloadableResourceBundleMessageSource().apply {
            setBasename("classpath:messages")
            setDefaultEncoding("UTF-8")
        }

    @Bean
    fun localeResolver(): LocaleResolver? =
        SessionLocaleResolver().apply {
            setDefaultLocale(Locale("en", "EN"))
        }
}
