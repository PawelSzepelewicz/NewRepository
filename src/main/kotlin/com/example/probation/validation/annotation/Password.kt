package com.example.probation.validation.annotation

import com.example.probation.validation.validator.PasswordValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Constraint(validatedBy = [PasswordValidator::class])
annotation class Password(
    val message: String = "{password.invalid}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
