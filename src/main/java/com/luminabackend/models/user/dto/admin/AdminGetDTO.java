package com.luminabackend.models.user.dto.admin;

import com.luminabackend.models.user.Admin;

import java.util.UUID;

public record AdminGetDTO(
        UUID id,
        String email,
        String firstName,
        String lastName
) {
    public AdminGetDTO(Admin admin){
        this(admin.getId(), admin.getEmail(), admin.getFirstName(), admin.getLastName());
    }
}
