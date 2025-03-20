package com.luminabackend.controllers.account;

import com.luminabackend.models.user.dto.user.UserLoginDTO;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.security.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountControllerDocumentation {
    @Operation(summary = "Login with email and password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully login",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenDTO.class)) }),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Incorrect or invalid credentials",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<TokenDTO> login(@Valid @RequestBody UserLoginDTO data);
}
