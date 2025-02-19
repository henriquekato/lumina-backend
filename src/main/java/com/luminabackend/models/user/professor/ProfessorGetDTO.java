package com.luminabackend.models.user.professor;

import java.util.UUID;

public record ProfessorGetDTO(
        UUID id,
        String name,
        String email
) {
    public ProfessorGetDTO(Professor professor){
        this(professor.getId(), professor.getName(), professor.getEmail());
    }
}
