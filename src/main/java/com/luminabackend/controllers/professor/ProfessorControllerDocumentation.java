package com.luminabackend.controllers.professor;

import com.luminabackend.models.user.dto.UserGetDTO;
import com.luminabackend.exceptions.errors.GeneralErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
public interface ProfessorControllerDocumentation {
    @Operation(summary = "Get a paginated list of professors")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of professors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserGetDTO.class))})
    })
    ResponseEntity<Page<UserGetDTO>> getPaginatedProfessors(Pageable page);
}
