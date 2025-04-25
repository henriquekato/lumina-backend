package com.luminabackend.controllers;

import com.luminabackend.exceptions.MissingFileException;
import com.luminabackend.models.education.material.Material;
import com.luminabackend.models.education.material.MaterialGetDTO;
import com.luminabackend.models.education.material.MaterialPostDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.services.FileStorageService;
import com.luminabackend.services.MaterialService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import com.luminabackend.utils.security.PayloadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
        @ApiResponse(
                responseCode = "403",
                description = "Access denied to this resource",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
        @ApiResponse(
                responseCode = "404",
                description = "Classroom not found",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) })
})
@RestController
@RequestMapping("/classroom/{classroomId}/material")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "Get a list of materials from a classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of materials",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/all")
    public ResponseEntity<List<Material>> getAllClassroomMaterials(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        return ResponseEntity.ok(materialService.getAllMaterials(classroomId, payloadDTO));
    }

    @Operation(summary = "Get a paginated list of materials from a classroom")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of materials",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<Page<Material>> getPaginatedClassroomMaterials(
            @PathVariable UUID classroomId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        return ResponseEntity.ok(materialService.getPaginatedClassroomMaterials(classroomId, payloadDTO, page));
    }

    @Operation(summary = "Create a new classroom material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a classroom material",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
           @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request part validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "409",
                    description = "Submission already sent to this task",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialGetDTO> createMaterial(
            @PathVariable UUID classroomId,
            @RequestPart("material") MaterialPostDTO materialPost,
            @RequestPart("files") List<MultipartFile> files,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        if (files == null || files.isEmpty()) {
            throw new MissingFileException("Missing material file");
        }

        Material savedMaterial = materialService.saveMaterial(
                                classroomId,
                                payloadDTO,
                                materialPost.title(),
                                materialPost.description(),
                                files);
        return ResponseEntity
                .created(linkTo(methodOn(MaterialController.class)
                        .getAllClassroomMaterials(classroomId, authorizationHeader))
                        .toUri())
                .body(new MaterialGetDTO(savedMaterial));
    }

    @Operation(summary = "Add a file to an existing material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added the file to the material",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaterialGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or material id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Material not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping(value = "/{materialId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialGetDTO> addFileToMaterial(
            @PathVariable UUID classroomId,
            @PathVariable UUID materialId,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        if (file == null || file.isEmpty()) {
            throw new MissingFileException("Missing file");
        }

        Material updatedMaterial = materialService.addFileToMaterial(materialId, classroomId, payloadDTO, file);
        return ResponseEntity.ok(new MaterialGetDTO(updatedMaterial));
    }

    @Operation(summary = "Delete a file from a material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully removed the file from the material",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MaterialGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, material id, or file id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Material or file not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('PROFESSOR')")
    @DeleteMapping("/{materialId}/file/{fileId}")
    public ResponseEntity<MaterialGetDTO> removeFileFromMaterial(
            @PathVariable UUID classroomId,
            @PathVariable UUID materialId,
            @PathVariable String fileId,
            @RequestHeader("Authorization") String authorizationHeader) throws FileNotFoundException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Material updatedMaterial = materialService.removeFileFromMaterial(materialId, classroomId, payloadDTO, fileId);
        return ResponseEntity.ok(new MaterialGetDTO(updatedMaterial));
    }

    @Operation(summary = "Delete a classroom material")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the classroom material"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or material id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom material not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('PROFESSOR')")
    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteMaterial(
            @PathVariable UUID classroomId,
            @PathVariable UUID materialId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        materialService.deleteById(materialId, classroomId, payloadDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a classroom material file")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the material file",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GridFsResource.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, material id, or file id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Classroom material not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{materialId}/file/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadMaterialFile(
            @PathVariable UUID classroomId,
            @PathVariable UUID materialId,
            @PathVariable String fileId,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        materialService.checkAccessToMaterial(classroomId, materialId, payloadDTO);

        GridFsResource file = fileStorageService.getFile(fileId);

        String filename = file.getFilename();
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        String contentDisposition = String.format(
                "attachment; filename=\"%s\"; filename*=UTF-8''%s",
                encodedFilename.replaceAll("[^\\x00-\\x7F]", "_"),
                encodedFilename
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(new ByteArrayResource(file.getInputStream().readAllBytes()));
    }

    @Operation(summary = "Get all classroom materials in a zip file")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the zip file",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GridFsResource.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) })
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadAllMaterialsFromClassroom(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        ByteArrayResource resource = materialService.getAllMaterialsAsZip(classroomId, payloadDTO);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=classroom-" + classroomId + "-materials.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
