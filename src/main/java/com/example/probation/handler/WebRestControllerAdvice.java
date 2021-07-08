package com.example.probation.handler;

import com.example.probation.model.ErrorsWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class WebRestControllerAdvice {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorsWrapper>> handleNotFoundException(ConstraintViolationException e) {
        List<ErrorsWrapper> errorsWrapper = new ArrayList<>();
        e.getConstraintViolations().forEach(cv -> errorsWrapper.
                add(new ErrorsWrapper(cv.getPropertyPath().toString(), cv.getMessage())));
        return new ResponseEntity<>(errorsWrapper, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
