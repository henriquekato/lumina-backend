package com.luminabackend.models.user.dto.professor;

import com.luminabackend.models.user.Professor;

import java.util.UUID;

public record ProfessorGetDTO(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
    public ProfessorGetDTO(Professor professor){
        this(professor.getId(), professor.getFirstName(), professor.getLastName(), professor.getEmail());
    }
}
