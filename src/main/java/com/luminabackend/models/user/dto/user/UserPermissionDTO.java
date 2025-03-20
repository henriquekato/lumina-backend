package com.luminabackend.models.user.dto.user;

import com.luminabackend.models.user.Role;
import com.luminabackend.utils.security.PayloadDTO;

import java.util.UUID;

public record UserPermissionDTO(UUID id, Role role) {
    public UserPermissionDTO(PayloadDTO payloadDTO) {
        this(payloadDTO.id(), payloadDTO.role());
    }
}
