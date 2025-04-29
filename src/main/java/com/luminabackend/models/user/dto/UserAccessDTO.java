package com.luminabackend.models.user.dto;

import com.luminabackend.models.user.Role;
import com.luminabackend.security.PayloadDTO;

import java.util.UUID;

public record UserAccessDTO(UUID id, Role role) {
    public UserAccessDTO(PayloadDTO payloadDTO) {
        this(payloadDTO.id(), payloadDTO.role());
    }
}
