package com.example.probation.exception;

public class TokenNotFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return "This token isn't valid.";
    }
}
