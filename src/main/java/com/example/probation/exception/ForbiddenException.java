package com.example.probation.exception;

public class ForbiddenException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Forbidden for none authorized users.";
    }
}
