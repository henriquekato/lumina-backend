package com.luminabackend.exceptions;

public class CannotDeleteLastAdministratorException extends RuntimeException {
    public CannotDeleteLastAdministratorException(String message) {
        super(message);
    }
}
