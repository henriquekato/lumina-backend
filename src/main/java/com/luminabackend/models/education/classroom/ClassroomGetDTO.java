package com.luminabackend.models.education.classroom;

import java.util.UUID;

public record ClassroomGetDTO (
        UUID id,
        String name,
        String description,
        UUID professorId
) {
    public ClassroomGetDTO(Classroom classroom) {
        this(
                classroom.getId(),
                classroom.getName(),
                classroom.getDescription(),
                classroom.getProfessorId());
    }
}
