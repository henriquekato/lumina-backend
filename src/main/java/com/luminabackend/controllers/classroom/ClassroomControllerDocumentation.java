package com.luminabackend.controllers.classroom;

import com.luminabackend.models.education.classroom.ClassroomGetDTO;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.classroom.ClassroomPutDTO;
import com.luminabackend.models.education.classroom.ClassroomWithRelationsDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
public interface ClassroomControllerDocumentation {
    @Operation(summary = "Get a paginated list of classrooms based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of classrooms",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) })
    })
    ResponseEntity<Page<ClassroomGetDTO>> getPaginatedClassrooms(Pageable page, @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Get a classroom by its id based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied to this classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<ClassroomGetDTO> getClassroom(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Get the classroom professor and a list of its students")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the professor and the list of students of the classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomWithRelationsDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied to this classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<ClassroomWithRelationsDTO> getClassroomWithRelations(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Create a new classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a classroom",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
    })
    ResponseEntity<ClassroomGetDTO> saveClassroom(
            @Valid @RequestBody ClassroomPostDTO classroomPostDTO,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Edit a classroom by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully edit the classroom",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
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
                    description = "Classroom not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<ClassroomGetDTO> editClassroom(
            @PathVariable UUID classroomId,
            @Valid @RequestBody ClassroomPutDTO classroomPutDTO);

    @Operation(summary = "Delete a classroom and its dependencies by classroom id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the classroom and its dependencies"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<Void> deleteClassroom(@PathVariable UUID classroomId);

    @Operation(summary = "Add a student to a classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully add the student to the classroom",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or student id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Professor does not manage this classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "409",
                    description = "Student already in this classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<Void> addStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Remove a student from classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully remove the student from the classroom"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or student id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "403",
                    description = "Professor does not manage this classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not in this classroom",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId,
            @RequestHeader("Authorization") String authorizationHeader);
}
