package com.luminabackend.models.user.dto.user;

import com.luminabackend.models.user.Role;
import com.luminabackend.utils.security.PayloadDTO;

import java.util.UUID;

public record UserAccessDTO(UUID id, Role role) {
    public UserAccessDTO(PayloadDTO payloadDTO) {
        this(payloadDTO.id(), payloadDTO.role());
    }
}
