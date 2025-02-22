package com.luminabackend.models.user.dto.student;

import com.luminabackend.models.user.Student;

import java.util.UUID;

public record StudentGetDTO(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
    public StudentGetDTO(Student student){
        this(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail());
    }
}
