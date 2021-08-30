package com.example.probation.validation.annotation

import com.example.probation.validation.validator.NewNameValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Constraint(validatedBy = [NewNameValidator::class])
annotation class UniqueNewName(
    val message: String = "{username.not.unique}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
