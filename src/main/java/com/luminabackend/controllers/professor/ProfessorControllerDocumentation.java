package com.luminabackend.controllers.professor;

import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
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
public interface ProfessorControllerDocumentation {
    @Operation(summary = "Get all professors")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list with all professors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class))})
    })
    ResponseEntity<List<ProfessorGetDTO>> getAllProfessors();

    @Operation(summary = "Get a paginated list of professors")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of professors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class))})
    })
    ResponseEntity<Page<ProfessorGetDTO>> getPaginatedProfessors(Pageable page);

    @Operation(summary = "Get a professor by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified professor",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid professor id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<ProfessorGetDTO> getProfessor(@PathVariable UUID id);

    @Operation(summary = "Create a new professor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a professor",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already in use",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<ProfessorGetDTO> saveProfessor(@Valid @RequestBody UserSignupDTO professorPostDTO);

    @Operation(summary = "Edit a professor by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully edit the professor",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid professor id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already in use",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<ProfessorGetDTO> editProfessor(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO);

    @Operation(summary = "Delete a professor by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the professor"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid professor id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete professor because they are currently assigned to one or more active classrooms",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<Void> deleteProfessor(@PathVariable UUID id);
}
