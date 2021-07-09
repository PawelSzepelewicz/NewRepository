package com.example.probation.handler;

import com.example.probation.model.ErrorsWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WebRestControllerAdvice {
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorsWrapper>> handleNotFoundException(final ConstraintViolationException e) {
        final List<ErrorsWrapper> errorsWrapper = e.getConstraintViolations().stream().map(cv ->
                new ErrorsWrapper(cv.getPropertyPath().toString(),
                        cv.getMessage())).collect(Collectors.toList());

        return new ResponseEntity<>(errorsWrapper, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
