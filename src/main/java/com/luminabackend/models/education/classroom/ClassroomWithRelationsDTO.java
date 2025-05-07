package com.luminabackend.models.education.classroom;

import com.luminabackend.models.user.dto.UserGetDTO;

import java.util.List;
import java.util.UUID;

public record ClassroomWithRelationsDTO (
        UUID id,
        UserGetDTO professor,
        List<UserGetDTO> students
) {
    public ClassroomWithRelationsDTO(UUID id, UserGetDTO professor, List<UserGetDTO> students){
        this.id = id;
        this.professor = professor;
        this.students = students;
    }
}
