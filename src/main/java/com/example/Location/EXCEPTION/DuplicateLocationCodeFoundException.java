package com.example.Location.EXCEPTION;

public class DuplicateLocationCodeFoundException extends RuntimeException {

    public DuplicateLocationCodeFoundException(String msg) {
        super(msg);
    }
}
