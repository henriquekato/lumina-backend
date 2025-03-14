package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.user.Role;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {
    public void checkProfessorAccessToClassroom(UUID professorId, Classroom classroom){
        if (!classroom.getProfessorId().equals(professorId))
            throw new AccessDeniedException("You don't have permission to access this class");
    }

    public void checkStudentAccessToClassroom(UUID studentId, Classroom classroom){
        if (!classroom.containsStudent(studentId))
            throw new AccessDeniedException("You don't have permission to access this class");
    }

    public void checkAccessToClassroom(PayloadDTO payloadDTO, Classroom classroom){
        if (payloadDTO.role().equals(Role.PROFESSOR)) checkProfessorAccessToClassroom(payloadDTO.id(), classroom);
        else if(payloadDTO.role().equals(Role.STUDENT)) checkStudentAccessToClassroom(payloadDTO.id(), classroom);
    }
}
