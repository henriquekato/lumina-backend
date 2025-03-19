package com.luminabackend.models.user.dto.user;

import com.luminabackend.models.user.Role;

import java.util.UUID;

public record UserAccessDTO(UUID id, Role role) {
}
