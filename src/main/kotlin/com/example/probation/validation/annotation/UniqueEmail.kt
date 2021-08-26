package com.example.probation.validation.annotation

import com.example.probation.validation.validator.EmailValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Constraint(validatedBy = [EmailValidator::class])
annotation class UniqueEmail(
    val message: String = "{email.not.unique}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
