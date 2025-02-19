package com.luminabackend.dtos.student;

import com.luminabackend.models.student.Student;

import java.util.UUID;

public record StudentDataDTO(
        UUID id,
        String name,
        String email
) {
    public StudentDataDTO(Student student){
        this(student.getId(), student.getName(), student.getEmail());
    }
}
