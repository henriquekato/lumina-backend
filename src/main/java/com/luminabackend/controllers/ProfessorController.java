package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.ProfessorService;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/professor")
public class ProfessorController {
    @Autowired
    private ProfessorService service;

    @Operation(summary = "Get a list of professors")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of professors",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class)) })
    })
    @GetMapping
    public ResponseEntity<List<ProfessorGetDTO>> getAllProfessors() {
        List<Professor> professors = service.getAllProfessors();
        return ResponseEntity.ok(professors.stream().map(ProfessorGetDTO::new).toList());
    }

    @Operation(summary = "Get a professor by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified professor",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> getProfessor(@PathVariable UUID id) {
        Optional<Professor> professorById = service.getProfessorById(id);
        return professorById.map(professor ->
                        ResponseEntity.ok(new ProfessorGetDTO(professor)))
                .orElseThrow(() -> new EntityNotFoundException(("Professor not found")));
    }

    @Operation(summary = "Create a new professor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Returns the created professor",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors",
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
    @PostMapping
    public ResponseEntity<ProfessorGetDTO> saveProfessor(@Valid @RequestBody UserSignupDTO professorPostDTO) {
        Professor newProfessor = service.save(professorPostDTO);
        return ResponseEntity.ok(new ProfessorGetDTO(newProfessor));
    }

    @Operation(summary = "Edit a professor by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the edited professor",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation errors",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor not found",
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
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> editProfessor(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO) {
        Professor professor = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new ProfessorGetDTO(professor));
    }

    @Operation(summary = "Delete a professor by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the professor"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
