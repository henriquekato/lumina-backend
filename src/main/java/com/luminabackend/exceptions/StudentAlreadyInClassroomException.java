package com.luminabackend.exceptions;

public class StudentAlreadyInClassroomException extends RuntimeException {
    public StudentAlreadyInClassroomException(String message) {
        super(message);
    }
}
