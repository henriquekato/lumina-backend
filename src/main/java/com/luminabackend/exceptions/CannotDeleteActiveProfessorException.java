package com.luminabackend.exceptions;

public class CannotDeleteActiveProfessorException extends RuntimeException{
    public CannotDeleteActiveProfessorException(String message) {
        super(message);
    }
}
