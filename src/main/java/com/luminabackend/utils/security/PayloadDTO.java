package com.luminabackend.utils.security;

import com.luminabackend.models.user.Role;

import java.util.UUID;

public record PayloadDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role
) {
}
