package com.luminabackend.utils.errors;

import org.springframework.validation.FieldError;

public record ValidationErrorsDTO(String field, String errorMsg){
    public ValidationErrorsDTO(FieldError error){
        this(error.getField(), error.getDefaultMessage());
    }
}
