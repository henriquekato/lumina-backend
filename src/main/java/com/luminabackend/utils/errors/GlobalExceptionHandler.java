package com.luminabackend.utils.errors;

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
    public ResponseEntity<List<ErrorResponseDTO>> handleError400(MethodArgumentNotValidException e){
        var errors = e.getFieldErrors();
        var errorsList = errors.stream().map(ErrorResponseDTO::new).toList();
        return ResponseEntity.badRequest().body(errorsList);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        if (ex.getRequiredType() == UUID.class) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("UUID", "Invalid UUID"));
        }
        return ResponseEntity.badRequest().body("Bad request");
    }
}
