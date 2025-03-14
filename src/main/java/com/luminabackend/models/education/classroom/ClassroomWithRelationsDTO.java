package com.luminabackend.models.education.classroom;

import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.student.StudentGetDTO;

import java.util.List;
import java.util.UUID;

public record ClassroomWithRelationsDTO (
        UUID id,
        ProfessorGetDTO professor,
        List<StudentGetDTO> students
) {
    public ClassroomWithRelationsDTO(UUID id, ProfessorGetDTO professor, List<StudentGetDTO> students){
        this.id = id;
        this.professor = professor;
        this.students = students;
    }
}
