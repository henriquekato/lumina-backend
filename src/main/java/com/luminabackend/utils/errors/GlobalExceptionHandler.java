package com.luminabackend.utils.errors;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ValidationErrorResponseDTO> handleError400(MethodArgumentNotValidException e){
        var errors = e.getFieldErrors();
        ValidationErrorResponseDTO errorResponseDTO = new ValidationErrorResponseDTO("Bad request: validation errors");
        errors.forEach(errorResponseDTO::addValidationError);
        return ResponseEntity.badRequest().body(errorResponseDTO);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() == UUID.class) {
            return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO("Invalid UUID"));
        }
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO("Bad request"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleBadCredentialsException(BadCredentialsException e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO("Incorrect username or password"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleEmailAlreadyInUseException(EmailAlreadyInUseException e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralErrorResponseDTO(e.getMessage()));
    }
}
