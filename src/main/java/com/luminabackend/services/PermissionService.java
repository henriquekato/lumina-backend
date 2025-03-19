package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {
    @Autowired
    private TaskService taskService;

    private void checkProfessorAccessToClassroom(UUID professorId, Classroom classroom){
        if (!classroom.getProfessorId().equals(professorId))
            throw new AccessDeniedException("You don't have permission to access this class");
    }

    private void checkStudentAccessToClassroom(UUID studentId, Classroom classroom){
        if (!classroom.containsStudent(studentId))
            throw new AccessDeniedException("You don't have permission to access this class");
    }

    public void checkAccessToClassroom(Classroom classroom, UserAccessDTO userAccessDTO){
        if (userAccessDTO.role().equals(Role.PROFESSOR)) checkProfessorAccessToClassroom(userAccessDTO.id(), classroom);
        else if(userAccessDTO.role().equals(Role.STUDENT)) checkStudentAccessToClassroom(userAccessDTO.id(), classroom);
    }

    public void checkAccessToTask(Classroom classroom, UUID taskId, UserAccessDTO userAccessDTO) {
        checkAccessToClassroom(classroom, userAccessDTO);
        if(!taskService.existsById(taskId))
            throw new EntityNotFoundException("Task not found");
    }
}
