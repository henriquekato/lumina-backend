package com.luminabackend.models.user.dto;

import com.luminabackend.models.user.User;

import java.util.UUID;

public record UserGetDTO(UUID id, String firstName, String lastName, String email, String role) {
    public UserGetDTO(User user){
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole().toString());
    }
}
