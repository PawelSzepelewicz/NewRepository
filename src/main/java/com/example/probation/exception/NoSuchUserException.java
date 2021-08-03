package com.example.probation.exception;

public class NoSuchUserException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Forbidden for can't be found such authorized user.";
    }
}
