package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.user.UserPermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {
    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassroomService classroomService;

    private void checkProfessorAccessToClassroom(UUID professorId, Classroom classroom){
        if (!classroom.getProfessorId().equals(professorId))
            throw new AccessDeniedException("You don't have permission to access this class");
    }

    private void checkStudentAccessToClassroom(UUID studentId, Classroom classroom){
        if (!classroom.containsStudent(studentId))
            throw new AccessDeniedException("You don't have permission to access this class");
    }

    public void checkAccessToClassroomById(UUID classroomId, UserPermissionDTO userPermissionDTO){
        Classroom classroom = classroomService.getClassroomById(classroomId);
        if (userPermissionDTO.role().equals(Role.PROFESSOR)) checkProfessorAccessToClassroom(userPermissionDTO.id(), classroom);
        else if(userPermissionDTO.role().equals(Role.STUDENT)) checkStudentAccessToClassroom(userPermissionDTO.id(), classroom);
    }

    public void checkAccessToClassroom(Classroom classroom, UserPermissionDTO userPermissionDTO){
        if (userPermissionDTO.role().equals(Role.PROFESSOR)) checkProfessorAccessToClassroom(userPermissionDTO.id(), classroom);
        else if(userPermissionDTO.role().equals(Role.STUDENT)) checkStudentAccessToClassroom(userPermissionDTO.id(), classroom);
    }

    public void checkAccessToTask(Classroom classroom, UUID taskId, UserPermissionDTO userPermissionDTO) {
        checkAccessToClassroom(classroom, userPermissionDTO);
        if(!taskService.existsById(taskId))
            throw new EntityNotFoundException("Task not found");
    }
}
