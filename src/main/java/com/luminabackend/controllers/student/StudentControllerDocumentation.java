package com.luminabackend.controllers.student;

import com.luminabackend.models.user.dto.student.StudentGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
public interface StudentControllerDocumentation {
    @Operation(summary = "Get all students")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list with all students",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentGetDTO.class)) })
    })
    ResponseEntity<List<StudentGetDTO>> getAllStudents();

    @Operation(summary = "Get a paginated list of students")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of students",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentGetDTO.class)) })
    })
    ResponseEntity<Page<StudentGetDTO>> getPaginatedStudents(Pageable page);

    @Operation(summary = "Get a student by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified student",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<StudentGetDTO> getStudent(@PathVariable UUID id);

    @Operation(summary = "Create a new student")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a student",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already in use",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<StudentGetDTO> saveStudent(@Valid @RequestBody UserSignupDTO studentPostDTO);

    @Operation(summary = "Edit a student by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully edit the student",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already in use",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<StudentGetDTO> editStudent(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO);

    @Operation(summary = "Delete a student by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the student"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<Void> deleteStudent(@PathVariable UUID id);
}
