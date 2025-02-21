package com.luminabackend.utils.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() == UUID.class) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("UUID", "Invalid UUID"));
        }
        return ResponseEntity.badRequest().body("Bad request");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e){
        return ResponseEntity.badRequest().body(new ErrorResponseDTO("auth", "Incorrect username or password"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e){
        return ResponseEntity.badRequest().body(new ErrorResponseDTO("auth", "Incorrect username or password"));
    }
}
