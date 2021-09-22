package com.example.probation.validation.annotation

import com.example.probation.validation.validator.EmailValidator
import com.example.probation.validation.validator.NewEmailValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Constraint(validatedBy = [EmailValidator::class, NewEmailValidator::class])
annotation class UniqueEmail(
    val message: String = "{email.not.unique}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
