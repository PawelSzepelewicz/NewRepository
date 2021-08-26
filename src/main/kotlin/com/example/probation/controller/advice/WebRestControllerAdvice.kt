package com.example.probation.controller.advice

import com.example.probation.core.dto.ErrorsWrapper
import com.example.probation.exception.*
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
import java.util.stream.Collectors
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class WebRestControllerAdvice : ResponseEntityExceptionHandler() {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleNotFoundException(e: ConstraintViolationException): ResponseEntity<List<ErrorsWrapper>> {
        val errorsWrapper = e.constraintViolations.stream()
            .map { cv: ConstraintViolation<*> ->
                ErrorsWrapper(
                    cv.propertyPath.toString(),
                    cv.message
                )
            }
            .collect(Collectors.toList())
        return ResponseEntity(errorsWrapper, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errorsWrappers: List<ErrorsWrapper> = ex.bindingResult.fieldErrors.stream()
            .map { cv: FieldError ->
                ErrorsWrapper(
                    cv.field,
                    cv.defaultMessage
                )
            }
            .collect(Collectors.toList())

        return ResponseEntity(errorsWrappers, HttpStatus.BAD_REQUEST)
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.FORBIDDEN)
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(
        NoSuchUserException::class
    )
    fun handleNoSuchUserException(ex: NoSuchUserException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.FORBIDDEN)
    }

    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(TimeHasExpiredException::class)
    fun handleTimeHasExpiredException(ex: TimeHasExpiredException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.PRECONDITION_FAILED)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TokenNotFoundException::class)
    fun handleTokenNotFoundException(ex: TokenNotFoundException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundByTokenException::class)
    fun handleUserNotFoundByTokenException(ex: UserNotFoundByTokenException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RoleNotFoundException::class)
    fun handleRoleNotFoundException(ex: RoleNotFoundException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Any> {
        ex.message
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }
}
