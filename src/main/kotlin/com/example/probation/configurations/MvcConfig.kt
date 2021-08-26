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
import java.util.*

@Configuration
class MvcConfig : WebMvcConfigurer {
    override fun getValidator(): Validator =
        LocalValidatorFactoryBean().let {
            it.setValidationMessageSource(messageSource())
            return it
        }

    @Bean
    fun messageSource(): MessageSource =
        ReloadableResourceBundleMessageSource().let {
            it.setBasename("classpath:messages")
            it.setDefaultEncoding("UTF-8")
            return it
        }

    @Bean
    fun localeResolver(): LocaleResolver? =
        SessionLocaleResolver().let {
            it.setDefaultLocale(Locale("en", "EN"))
            return it
        }
}
