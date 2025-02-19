package com.luminabackend.dtos.professor;

import com.luminabackend.models.professor.Professor;

import java.util.UUID;

public record ProfessorDataDTO(
        UUID id,
        String name,
        String email
) {
    public ProfessorDataDTO(Professor professor){
        this(professor.getId(), professor.getName(), professor.getEmail());
    }
}
