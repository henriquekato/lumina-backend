package com.luminabackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorsDTO>> handleError400(MethodArgumentNotValidException e){
        var errors = e.getFieldErrors();
        var errorsList = errors.stream().map(ValidationErrorsDTO::new).toList();
        return ResponseEntity.badRequest().body(errorsList);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        if (ex.getRequiredType() == UUID.class) {
            return new ResponseEntity<>("Invalid UUID", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }
}
