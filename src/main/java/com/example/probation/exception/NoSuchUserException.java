package com.example.probation.exception;

public class NoSuchUserException extends RuntimeException {
    public String message;
    public NoSuchUserException(String msg){
        this.message = msg;
    }
}
