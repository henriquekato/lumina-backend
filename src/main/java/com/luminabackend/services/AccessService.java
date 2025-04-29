package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.UserAccessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccessService {
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

    public void checkAccessToClassroomById(UUID classroomId, UserAccessDTO userAccessDTO){
        Classroom classroom = classroomService.getClassroomById(classroomId);
        checkAccessToClassroom(classroom, userAccessDTO);
    }

    public void checkAccessToClassroom(Classroom classroom, UserAccessDTO userAccessDTO){
        if (userAccessDTO.role().equals(Role.PROFESSOR)) checkProfessorAccessToClassroom(userAccessDTO.id(), classroom);
        else if(userAccessDTO.role().equals(Role.STUDENT)) checkStudentAccessToClassroom(userAccessDTO.id(), classroom);
    }

    public void checkStudentAccessToSubmissionById(UUID submissionId, UserAccessDTO userAccessDTO){
        Submission submission = submissionService.getSubmissionById(submissionId);
        checkStudentAccessToSubmission(submission, userAccessDTO);
    }

    public void checkStudentAccessToSubmission(Submission submission, UserAccessDTO userAccessDTO){
        if (userAccessDTO.role().equals(Role.STUDENT) && !submission.getStudentId().equals(userAccessDTO.id()))
            throw new AccessDeniedException("You don't have permission to access this resource");
    }
}
