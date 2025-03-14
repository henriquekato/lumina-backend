package com.luminabackend.exceptions;

public class TaskDueDateExpiredException  extends RuntimeException {
    public TaskDueDateExpiredException(String message) {
        super(message);
    }
}
