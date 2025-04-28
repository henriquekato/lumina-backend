package com.luminabackend.controllers.admin;

import com.luminabackend.models.user.dto.admin.AdminGetDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
public interface AdminControllerDocumentation {
    @Operation(summary = "Get a paginated list of admins")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of admins",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class))})
    })
    ResponseEntity<Page<AdminGetDTO>> getPaginatedAdmins(Pageable page);

    @Operation(summary = "Get a admin by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified admin",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid admin id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Admin not found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
    })
    ResponseEntity<AdminGetDTO> getAdmin(@PathVariable UUID id);

    @Operation(summary = "Create a new admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create an admin",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class))}),
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
    ResponseEntity<AdminGetDTO> saveAdmin(@Valid @RequestBody UserSignupDTO adminPostDTO);

    @Operation(summary = "Edit a admin by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully edit the admin",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminGetDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid admin id",
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
                    description = "Admin not found",
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
    ResponseEntity<AdminGetDTO> editAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO);

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
    ResponseEntity<Void> deleteAdmin(@PathVariable UUID id);
}
