package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {
    @Mock
    ClassroomService classroomService;

    @Mock
    SubmissionService submissionService;

    @InjectMocks
    AccessService sut;

    static Student student;
    static Professor professor;
    static Classroom classroomWithAccess;
    static Classroom classroomWithoutAccess;
    static Submission submissionWithAccess;
    static Submission submissionWithoutAccess;

    @BeforeAll
    static void beforeAll() {
        student = new Student("jane@mail.com", "23dfa34fga", "Jane", "Doe");
        professor = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");

        classroomWithAccess = new Classroom(new ClassroomPostDTO("class 1", "class 1", professor.getId()));
        classroomWithAccess.addStudent(student.getId());

        classroomWithoutAccess = new Classroom(new ClassroomPostDTO("class 1", "class 1", UUID.randomUUID()));

        submissionWithAccess = new Submission(new SubmissionPostDTO("first submission"), UUID.randomUUID(), student.getId(), "fileid");
        submissionWithoutAccess = new Submission(new SubmissionPostDTO("second submission"), UUID.randomUUID(), UUID.randomUUID(), "fileid");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should professor have access to classroom by id")
    void shouldProfessorHaveAccessToClassroomById() {
        when(classroomService.getClassroomById(classroomWithAccess.getId())).thenReturn(classroomWithAccess);

        assertThatNoException().isThrownBy(()->sut.checkAccessToClassroomById(classroomWithAccess.getId(), new UserAccessDTO(professor.getId(), Role.PROFESSOR)));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should professor not have access to classroom by id")
    void shouldProfessorNotHaveAccessToClassroomById() {
        when(classroomService.getClassroomById(classroomWithoutAccess.getId())).thenReturn(classroomWithoutAccess);

        assertThatThrownBy(()->sut.checkAccessToClassroomById(classroomWithoutAccess.getId(), new UserAccessDTO(professor.getId(), Role.PROFESSOR))).isInstanceOf(AccessDeniedException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should student have access to classroom by id")
    void shouldStudentHaveAccessToClassroomById() {
        when(classroomService.getClassroomById(classroomWithAccess.getId())).thenReturn(classroomWithAccess);

        assertThatNoException().isThrownBy(()->sut.checkAccessToClassroomById(classroomWithAccess.getId(), new UserAccessDTO(student.getId(), Role.STUDENT)));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should student not have access to classroom by id")
    void shouldStudentNotHaveAccessToClassroomById() {
        when(classroomService.getClassroomById(classroomWithoutAccess.getId())).thenReturn(classroomWithoutAccess);

        assertThatThrownBy(()->sut.checkAccessToClassroomById(classroomWithoutAccess.getId(), new UserAccessDTO(student.getId(), Role.STUDENT))).isInstanceOf(AccessDeniedException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should student have access to submission by id")
    void shouldStudentHaveAccessToSubmissionById() {
        when(submissionService.getSubmissionById(submissionWithAccess.getId())).thenReturn(submissionWithAccess);

        assertThatNoException().isThrownBy(()->sut.checkStudentAccessToSubmissionById(submissionWithAccess.getId(), new UserAccessDTO(student.getId(), Role.STUDENT)));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should student not have access to submission by id")
    void shouldStudentNotHaveAccessToSubmissionById() {
        when(submissionService.getSubmissionById(submissionWithoutAccess.getId())).thenReturn(submissionWithoutAccess);

        assertThatThrownBy(()->sut.checkStudentAccessToSubmissionById(submissionWithoutAccess.getId(), new UserAccessDTO(student.getId(), Role.STUDENT))).isInstanceOf(AccessDeniedException.class);
    }
}
