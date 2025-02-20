package com.luminabackend.utils.errors;

import org.springframework.validation.FieldError;

public record ErrorResponseDTO(String field, String errorMsg){
    public ErrorResponseDTO(FieldError error){
        this(error.getField(), error.getDefaultMessage());
    }
}
