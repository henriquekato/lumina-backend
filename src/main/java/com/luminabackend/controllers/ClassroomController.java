package com.luminabackend.controllers;

import com.luminabackend.models.education.classroom.*;
import com.luminabackend.models.user.dto.user.UserPermissionDTO;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.PermissionService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import com.luminabackend.utils.security.PayloadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
@RestController
@RequestMapping("/classroom")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Get a list of all classrooms based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of all classrooms",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) })
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/all")
    public ResponseEntity<List<ClassroomGetDTO>> getAllClassrooms(@RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        List<Classroom> classrooms = classroomService.getClassroomsBasedOnUserPermission(new UserPermissionDTO(payloadDTO));
        return ResponseEntity.ok(classrooms.stream().map(ClassroomGetDTO::new).toList());
    }

    @Operation(summary = "Get a paginated list of classrooms based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of classrooms",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClassroomGetDTO.class)) })
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<Page<ClassroomGetDTO>> getPaginatedClassrooms(Pageable page, @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Page<Classroom> classrooms = classroomService.getPaginatedClassroomsBasedOnUserPermission(new UserPermissionDTO(payloadDTO), page);
        return ResponseEntity.ok(classrooms.map(ClassroomGetDTO::new));
    }

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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}")
    public ResponseEntity<ClassroomGetDTO> getClassroom(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.getClassroomBasedOnUserPermission(classroomId, new UserPermissionDTO(payloadDTO));
        return ResponseEntity.ok(new ClassroomGetDTO(classroom));
    }

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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}/members")
    public ResponseEntity<ClassroomWithRelationsDTO> getClassroomWithRelations(
        @PathVariable UUID classroomId,
        @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToClassroom(classroom, new UserPermissionDTO(payloadDTO));
        ClassroomWithRelationsDTO fullClassroom = classroomService.getClassroomWithRelations(classroom);
        return ResponseEntity.ok(fullClassroom);
    }

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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassroomGetDTO> saveClassroom(
            @Valid @RequestBody ClassroomPostDTO classroomPostDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        Classroom savedClassroom = classroomService.save(classroomPostDTO);
        return ResponseEntity
                .created(linkTo(methodOn(ClassroomController.class)
                        .getClassroom(savedClassroom.getId(), authorizationHeader))
                        .toUri())
                .body(new ClassroomGetDTO(savedClassroom));
    }

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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{classroomId}")
    public ResponseEntity<ClassroomGetDTO> editClassroom(
            @PathVariable UUID classroomId,
            @Valid @RequestBody ClassroomPutDTO classroomPutDTO) {
        Classroom classroom = classroomService.edit(classroomId, classroomPutDTO);
        return ResponseEntity.ok(new ClassroomGetDTO(classroom));
    }

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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable UUID classroomId) {
        classroomService.deleteById(classroomId);
        return ResponseEntity.noContent().build();
    }

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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Void> addStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.getClassroomBasedOnUserPermission(classroomId, new UserPermissionDTO(payloadDTO));
        classroomService.addStudentToClassroom(studentId, classroom);
        return ResponseEntity.ok().build();
    }

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
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.getClassroomBasedOnUserPermission(classroomId, new UserPermissionDTO(payloadDTO));
        classroomService.removeStudentFromClassroom(studentId, classroom);
        return ResponseEntity.noContent().build();
    }
}
