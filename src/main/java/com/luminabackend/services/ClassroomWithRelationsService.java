package com.luminabackend.services;

import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomWithRelationsDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomWithRelationsService {
    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfessorService professorService;

    public ClassroomWithRelationsDTO getClassroomWithRelations(Classroom classroom){
        Optional<Professor> professorById = professorService.getProfessorById(classroom.getProfessorId());
        ProfessorGetDTO professor = professorById
                .map(ProfessorGetDTO::new)
                .orElseThrow(IllegalStateException::new);

        List<StudentGetDTO> students = studentService
                .getAllStudentsById(classroom.getStudentsIds())
                .stream()
                .map(StudentGetDTO::new)
                .toList();

        return new ClassroomWithRelationsDTO(classroom.getId(), professor, students);
    }
}
