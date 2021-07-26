package com.example.probation.exception;

public class ForbiddenException extends RuntimeException {
    public String getMessage() {
        return "Forbidden for none authorized users.";
    }
}
