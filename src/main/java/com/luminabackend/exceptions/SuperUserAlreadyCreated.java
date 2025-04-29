package com.luminabackend.exceptions;

public class SuperUserAlreadyCreated extends RuntimeException{
    public SuperUserAlreadyCreated(String message) {
        super(message);
    }
}
