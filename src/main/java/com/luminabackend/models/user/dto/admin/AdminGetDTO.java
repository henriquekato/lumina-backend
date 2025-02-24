package com.luminabackend.models.user.dto.admin;

import com.luminabackend.models.user.Admin;

import java.util.UUID;

public record AdminGetDTO(
        UUID id,
        String email
) {
    public AdminGetDTO(Admin admin){
        this(admin.getId(), admin.getEmail());
    }
}
