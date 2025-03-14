package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.dto.admin.AdminGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.AdminService;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService service;

    @Operation(summary = "Get all admins")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list with all admins",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class)) })
    })
    @GetMapping("/all")
    public ResponseEntity<List<AdminGetDTO>> getAllAdmins() {
        List<Admin> admins = service.getAllAdmins();
        return ResponseEntity.ok(admins.stream().map(AdminGetDTO::new).toList());
    }

    @Operation(summary = "Get a paginated list of admins")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of admins",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class)) })
    })
    @GetMapping
    public ResponseEntity<Page<AdminGetDTO>> getPaginatedAdmins(Pageable page) {
        Page<Admin> admins = service.getPaginatedAdmins(page);
        return ResponseEntity.ok(admins.map(AdminGetDTO::new));
    }

    @Operation(summary = "Get a admin by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified admin",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid admin id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Admin not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdminGetDTO> getAdmin(@PathVariable UUID id) {
        Optional<Admin> adminById = service.getAdminById(id);
        return adminById.map(admin ->
                ResponseEntity.ok(new AdminGetDTO(admin)))
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
    }

    @Operation(summary = "Create a new admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create an admin",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class)) }),
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
    @PostMapping
    public ResponseEntity<AdminGetDTO> saveAdmin(@Valid @RequestBody UserSignupDTO adminPostDTO) {
        Admin newAdmin = service.save(adminPostDTO);
        return ResponseEntity
                .created(linkTo(methodOn(AdminController.class)
                        .getAdmin(newAdmin.getId()))
                        .toUri())
                .body(new AdminGetDTO(newAdmin));
    }

    @Operation(summary = "Edit a admin by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully edit the admin",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid admin id",
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
                    description = "Admin not found",
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
    public ResponseEntity<AdminGetDTO> editAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO) {
        Admin admin = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new AdminGetDTO(admin));
    }

    @Operation(summary = "Delete a admin by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the admin"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid admin id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "The last administrator cannot be deleted",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Admin not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
