package com.example.probation.validation.annotation

import com.example.probation.validation.validator.NameValidator
import com.example.probation.validation.validator.NewNameValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Constraint(validatedBy = [NameValidator::class, NewNameValidator::class])
annotation class UniqueUsername(
    val message: String = "{username.not.unique}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
