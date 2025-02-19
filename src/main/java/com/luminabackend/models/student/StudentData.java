package com.luminabackend.models.student;

import java.util.UUID;

public record StudentData(
        UUID id,
        String name,
        String email
) {
    public StudentData(Student student){
        this(student.getId(), student.getName(), student.getEmail());
    }
}
