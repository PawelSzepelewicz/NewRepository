package com.example.probation.exception;

public class NoUserForbiddenException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Forbidden for can't be found such authorized user.";
    }
}
