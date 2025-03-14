package com.luminabackend.exceptions;

public class TaskAlreadySubmittedException extends RuntimeException{
    public TaskAlreadySubmittedException(String message) {
        super(message);
    }
}
