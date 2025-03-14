package com.luminabackend.exceptions;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException() {
        super("This email address is already registered");
    }
}
