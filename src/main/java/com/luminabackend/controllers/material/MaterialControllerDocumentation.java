package com.luminabackend.controllers.material;

import com.luminabackend.models.education.material.Material;
import com.luminabackend.models.education.material.MaterialGetDTO;
import com.luminabackend.models.education.material.MaterialPostDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.exceptions.errors.GeneralErrorResponseDTO;
import com.luminabackend.exceptions.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = {@Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
        @ApiResponse(
                responseCode = "403",
                description = "Access denied to this resource",
                content = {@Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
        @ApiResponse(
                responseCode = "404",
                description = "Classroom not found",
                content = {@Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class))})
})
public interface MaterialControllerDocumentation {
    @Operation(summary = "Get a list of materials from a classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of materials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<List<Material>> getAllClassroomMaterials(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Get a paginated list of materials from a classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of materials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<Page<Material>> getPaginatedClassroomMaterials(
            @PathVariable UUID classroomId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Create a new classroom material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a classroom material",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request part validation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "409",
                    description = "Submission already sent to this task",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<MaterialGetDTO> createMaterial(
            @PathVariable UUID classroomId,
            @RequestPart("material") MaterialPostDTO materialPost,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException;

    @Operation(summary = "Delete a classroom material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the classroom material"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or material id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom material not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<Void> deleteMaterial(
            @PathVariable UUID classroomId,
            @PathVariable UUID materialId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Get a classroom material file")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the material file",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GridFsResource.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, material id, or file id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom material not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<ByteArrayResource> downloadMaterialFile(
            @PathVariable UUID classroomId,
            @PathVariable UUID materialId,
            @PathVariable String fileId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException;

    @Operation(summary = "Get all classroom materials in a zip file")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the zip file",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GridFsResource.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))})
    })
    ResponseEntity<ByteArrayResource> downloadAllMaterialsFromClassroom(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException;
}
