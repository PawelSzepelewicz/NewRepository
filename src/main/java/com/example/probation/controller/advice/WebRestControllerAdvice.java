package com.example.probation.controller.advice;

import com.example.probation.exception.ForbiddenException;
import com.example.probation.model.ErrorsWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WebRestControllerAdvice extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorsWrapper>> handleNotFoundException(final ConstraintViolationException e) {
        final List<ErrorsWrapper> errorsWrapper = e.getConstraintViolations().stream().map(cv ->
                new ErrorsWrapper(cv.getPropertyPath().toString(),
                        cv.getMessage())).collect(Collectors.toList());

        return new ResponseEntity<>(errorsWrapper, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final List<ErrorsWrapper> errorsWrappers = ex.getBindingResult().getFieldErrors().stream().map(cv ->
                new ErrorsWrapper(cv.getField(), cv.getDefaultMessage())).collect(Collectors.toList());

        return new ResponseEntity<>(errorsWrappers, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(final ForbiddenException fe) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
