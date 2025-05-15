package com.luminabackend.exceptions.errors;

import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponseDTO {
    private final String message;
    private final List<ErrorValidationDTO> validationErrors;

    public ValidationErrorResponseDTO(String message){
        this.message = message;
        validationErrors = new ArrayList<>();
    }

    public void addValidationError(FieldError error){
        validationErrors.add(new ErrorValidationDTO(error.getField(), error.getDefaultMessage()));
    }

    record ErrorValidationDTO(String field, String errorMsg){}
}
