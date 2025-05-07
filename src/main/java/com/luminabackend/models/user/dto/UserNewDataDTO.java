package com.luminabackend.models.user.dto;

import com.luminabackend.models.user.Role;

public record UserNewDataDTO(String email, String password, String firstName, String lastName, Role role) {
}
