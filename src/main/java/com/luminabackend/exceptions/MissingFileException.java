package com.luminabackend.exceptions;

public class MissingFileException extends RuntimeException{
    public MissingFileException(String message) {
        super(message);
    }
}
