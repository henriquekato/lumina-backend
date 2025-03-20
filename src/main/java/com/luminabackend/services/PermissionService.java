package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.user.UserPermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissionService {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private SubmissionService submissionService;

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

    public void checkStudentAccessToSubmission(Submission submission, UserPermissionDTO userPermissionDTO){
        if (userPermissionDTO.role().equals(Role.STUDENT) && !submission.getStudentId().equals(userPermissionDTO.id()))
            throw new AccessDeniedException("You don't have permission to access this resource");
    }

    public void checkStudentAccessToSubmissionById(UUID submissionId, UserPermissionDTO userPermissionDTO){
        Submission submission = submissionService.getSubmissionById(submissionId);
        if (userPermissionDTO.role().equals(Role.STUDENT) && !submission.getStudentId().equals(userPermissionDTO.id()))
            throw new AccessDeniedException("You don't have permission to access this resource");
    }
}
