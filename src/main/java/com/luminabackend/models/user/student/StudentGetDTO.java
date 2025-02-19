package com.luminabackend.models.user.student;

import java.util.UUID;

public record StudentGetDTO(
        UUID id,
        String name,
        String email
) {
    public StudentGetDTO(Student student){
        this(student.getId(), student.getName(), student.getEmail());
    }
}
