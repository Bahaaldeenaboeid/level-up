package com.yourstore.core.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String value) {
        super(resourceName + " already exists with value: " + value);
    }
}