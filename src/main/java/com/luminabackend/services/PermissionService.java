package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.user.Role;
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

    public void checkAccessToClassroom(Classroom classroom, PayloadDTO payloadDTO){
        if (payloadDTO.role().equals(Role.PROFESSOR)) checkProfessorAccessToClassroom(payloadDTO.id(), classroom);
        else if(payloadDTO.role().equals(Role.STUDENT)) checkStudentAccessToClassroom(payloadDTO.id(), classroom);
    }

    public void checkAccessToTask(Classroom classroom, UUID taskId, PayloadDTO payloadDTO) {
        checkAccessToClassroom(classroom, payloadDTO);
        if(!taskService.existsById(taskId))
            throw new EntityNotFoundException("Task not found");
    }
}
