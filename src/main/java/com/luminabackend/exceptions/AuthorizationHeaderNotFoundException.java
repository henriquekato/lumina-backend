package com.luminabackend.exceptions;

public class AuthorizationHeaderNotFoundException extends RuntimeException {
    public AuthorizationHeaderNotFoundException(String message) {
        super(message);
    }
}
