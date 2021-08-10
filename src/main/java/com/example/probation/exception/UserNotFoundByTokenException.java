package com.example.probation.exception;

public class UserNotFoundByTokenException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Can not find any user by this token.";
    }
}
