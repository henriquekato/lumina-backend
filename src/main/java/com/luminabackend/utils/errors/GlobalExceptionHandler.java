package com.luminabackend.utils.errors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.luminabackend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationError(MethodArgumentNotValidException e){
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleMethodArgumentTypeMismatch(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType() == UUID.class) {
                return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO("Invalid UUID"));
            }
        }
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO("Bad request"));
    }

    @ExceptionHandler(CannotDeleteLastAdministratorException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleCannotDeleteLastAdministratorException(CannotDeleteLastAdministratorException e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(SuperUserAlreadyCreated.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleSuperUserAlreadyCreated(SuperUserAlreadyCreated e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(CannotDeleteActiveProfessorException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleCannotDeleteActiveProfessorException(CannotDeleteActiveProfessorException e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(MissingFileException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleMissingFileException(MissingFileException e) {
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(TaskDueDateExpiredException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleTaskDueDateExpiredException(TaskDueDateExpiredException e){
        return ResponseEntity.badRequest().body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleBadCredentialsException(BadCredentialsException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GeneralErrorResponseDTO("Incorrect username or password"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleAccessDeniedException(AccessDeniedException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleEntityNotFoundException(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleEmailAlreadyInUseException(EmailAlreadyInUseException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(StudentAlreadyInClassroomException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleStudentAlreadyInClassroomException(StudentAlreadyInClassroomException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(TaskAlreadySubmittedException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleTaskAlreadySubmittedException(TaskAlreadySubmittedException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new GeneralErrorResponseDTO(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GeneralErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GeneralErrorResponseDTO("Internal server error"));
    }
}
