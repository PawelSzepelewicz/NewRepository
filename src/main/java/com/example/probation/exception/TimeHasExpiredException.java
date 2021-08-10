package com.example.probation.exception;

public class TimeHasExpiredException extends RuntimeException {
    @Override
    public String getMessage() {
        return "This token has expired time to confirm.";
    }
}
