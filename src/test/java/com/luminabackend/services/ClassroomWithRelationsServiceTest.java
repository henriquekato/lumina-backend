package com.luminabackend.services;

import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.classroom.ClassroomWithRelationsDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassroomWithRelationsServiceTest {
    @Mock
    StudentService studentService;

    @Mock
    ProfessorService professorService;

    @InjectMocks
    ClassroomWithRelationsService sut;

    static Student student1;
    static Student student2;
    static Professor professor;
    static Classroom classroom;

    @BeforeAll
    static void beforeAll() {
        student1 = new Student("jane@mail.com", "23dfa34fga", "Jane", "Doe");
        student2 = new Student("jorge@mail.com", "gfsdgsdfg45", "Jorge", "Doe");
        professor = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
        classroom = new Classroom(new ClassroomPostDTO("class 1", "class 1", professor.getId()));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should get classroom only with professor")
    void shouldGetClassroomOnlyWithProfessor() {
        when(professorService.getProfessorById(classroom.getProfessorId())).thenReturn(Optional.ofNullable(professor));
        assertThat(sut.getClassroomWithRelations(classroom)).isEqualTo(new ClassroomWithRelationsDTO(classroom.getId(), new ProfessorGetDTO(professor), List.of()));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should get classrom with professor and one student")
    void shouldGetClassromWithProfessorAndOneStudent() {
        classroom.addStudent(student1.getId());

        when(professorService.getProfessorById(classroom.getProfessorId())).thenReturn(Optional.ofNullable(professor));
        when(studentService.getAllStudentsById(classroom.getStudentsIds())).thenReturn(List.of(student1));

        assertThat(sut.getClassroomWithRelations(classroom)).isEqualTo(new ClassroomWithRelationsDTO(classroom.getId(), new ProfessorGetDTO(professor), List.of(new StudentGetDTO(student1))));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should get classrom with professor and students")
    void shouldGetClassromWithProfessorAndStudents() {
        classroom.addStudent(student1.getId());
        classroom.addStudent(student2.getId());

        when(professorService.getProfessorById(classroom.getProfessorId())).thenReturn(Optional.ofNullable(professor));
        when(studentService.getAllStudentsById(classroom.getStudentsIds())).thenReturn(List.of(student1, student2));

        assertThat(sut.getClassroomWithRelations(classroom)).isEqualTo(new ClassroomWithRelationsDTO(classroom.getId(), new ProfessorGetDTO(professor), List.of(new StudentGetDTO(student1), new StudentGetDTO(student2))));
    }
}
