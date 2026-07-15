package com.yourstore.core.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String field, String message) {
        super("Invalid " + field + ": " + message);
    }
}