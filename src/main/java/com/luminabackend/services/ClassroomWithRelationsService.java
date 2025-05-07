package com.luminabackend.services;

import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomWithRelationsDTO;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomWithRelationsService {
    @Autowired
    private AdminService service;

    public ClassroomWithRelationsDTO getClassroomWithRelations(Classroom classroom){
        Optional<User> professorById = service.getUserById(classroom.getProfessorId());
        UserGetDTO professor = professorById
                .map(UserGetDTO::new)
                .orElseThrow(IllegalStateException::new);

        List<UserGetDTO> students = service
                .getUsersById(classroom.getStudentsIds())
                .stream()
                .map(UserGetDTO::new)
                .toList();

        return new ClassroomWithRelationsDTO(classroom.getId(), professor, students);
    }
}
