package com.example.probation.validation.annotation

import com.example.probation.validation.validator.NameValidator
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass


@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Constraint(validatedBy = [NameValidator::class])
annotation class UniqueUsername(
    val message: String = "{username.not.unique}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
