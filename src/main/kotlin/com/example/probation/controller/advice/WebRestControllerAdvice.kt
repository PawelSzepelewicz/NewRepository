package com.example.probation.controller.advice

import com.example.probation.core.dto.ErrorsWrapper
import com.example.probation.exception.EntityNotFoundException
import com.example.probation.exception.ForbiddenException
import com.example.probation.exception.UserNotFoundException
import com.example.probation.exception.PasswordDoesNotMatchesException
import com.example.probation.exception.RoleNotFoundException
import com.example.probation.exception.TimeHasExpiredException
import com.example.probation.exception.TokenNotFoundException
import com.example.probation.exception.UserNotFoundByTokenException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class WebRestControllerAdvice : ResponseEntityExceptionHandler() {
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleNotFoundException(e: ConstraintViolationException) =
        e.constraintViolations.map { cv: ConstraintViolation<*> ->
            ErrorsWrapper(
                cv.propertyPath.toString(),
                cv.message
            )
        }
            .let { ResponseEntity(it, HttpStatus.UNPROCESSABLE_ENTITY) }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = ex.bindingResult.fieldErrors.map { cv: FieldError ->
        ErrorsWrapper(
            cv.field,
            cv.defaultMessage
        )
    }
        .let { ResponseEntity(it, HttpStatus.BAD_REQUEST) }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.FORBIDDEN)

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(
        UserNotFoundException::class
    )
    fun handleNoSuchUserException(ex: UserNotFoundException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.FORBIDDEN)

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(TimeHasExpiredException::class)
    fun handleTimeHasExpiredException(ex: TimeHasExpiredException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.PRECONDITION_FAILED)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenNotFoundException::class)
    fun handleTokenNotFoundException(ex: TokenNotFoundException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.NOT_FOUND)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundByTokenException::class)
    fun handleUserNotFoundByTokenException(ex: UserNotFoundByTokenException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.NOT_FOUND)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RoleNotFoundException::class)
    fun handleRoleNotFoundException(ex: RoleNotFoundException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Any> =
        ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(PasswordDoesNotMatchesException::class)
    fun handlePasswordDoesNotMatchesException(ex: PasswordDoesNotMatchesException): ResponseEntity<Any> =
        mutableListOf(ErrorsWrapper("oldPassword", ex.message)).let {
            return ResponseEntity(it, HttpStatus.FORBIDDEN)
        }
}
