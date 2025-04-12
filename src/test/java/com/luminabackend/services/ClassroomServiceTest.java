package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.StudentAlreadyInClassroomException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.classroom.ClassroomPutDTO;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.repositories.classroom.ClassroomRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {
    @Mock
    ClassroomRepository repository;

    @Mock
    TaskService taskService;

    @Mock
    MaterialService materialService;

    @InjectMocks
    ClassroomService sut;

    static Student student1;
    static Student student2;
    static Professor professor1;
    static Professor professor2;
    static Admin admin;
    static Classroom classroom1;
    static Classroom classroom2;
    static Classroom classroom3;
    static UserAccessDTO student1UserAccess;
    static UserAccessDTO student2UserAccess;
    static UserAccessDTO professor1UserAccess;
    static UserAccessDTO professor2UserAccess;
    static UserAccessDTO adminUserAccess;

    @BeforeAll
    static void beforeAll() {
        student1 = new Student("jane@mail.com", "23dfa34fga", "Jane", "Doe");
        student2 = new Student("jorge@mail.com", "gfsdgsdfg45", "Jorge", "Doe");
        professor1 = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
        professor2 = new Professor("paulo@mail.com", "fsadfsf3", "Paulo", "Doe");
        admin = new Admin("admin@email.com", "fadjsk3fjask", "Admin", "Admin");
        student1UserAccess = new UserAccessDTO(student1.getId(), Role.STUDENT);
        student2UserAccess = new UserAccessDTO(student2.getId(), Role.STUDENT);
        professor1UserAccess = new UserAccessDTO(professor1.getId(), Role.PROFESSOR);
        professor2UserAccess = new UserAccessDTO(professor2.getId(), Role.PROFESSOR);
        adminUserAccess = new UserAccessDTO(admin.getId(), Role.ADMIN);
    }

    @BeforeEach
    void beforeEach(){
        classroom1 = new Classroom(new ClassroomPostDTO("class 1", "class 1", professor1.getId()));
        classroom2 = new Classroom(new ClassroomPostDTO("class 2", "class 2", professor2.getId()));
        classroom3 = new Classroom(new ClassroomPostDTO("class 3", "class 3", professor2.getId()));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return all classes to admin")
    void shouldReturnAllClassesToAdmin() {
        List<Classroom> classrooms = List.of(classroom1, classroom2, classroom3);
        when(repository.findAll()).thenReturn(classrooms);

        assertThat(sut.getClassroomsBasedOnUserAccess(adminUserAccess)).isEqualTo(classrooms);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return professor classrooms")
    void shouldReturnProfessorClasses() {
        when(repository.findAllByProfessorId(professor1UserAccess.id())).thenReturn(List.of(classroom1));

        assertThat(sut.getClassroomsBasedOnUserAccess(professor1UserAccess)).isEqualTo(List.of(classroom1));
        verify(repository, times(1)).findAllByProfessorId(professor1UserAccess.id());
    }

    @Test
    @DisplayName("should return student classrooms")
    void shouldReturnStudentClassrooms() {
        classroom3.addStudent(student1.getId());
        when(repository.findAllByStudentsIdsContains(student1UserAccess.id())).thenReturn(List.of(classroom3));

        assertThat(sut.getClassroomsBasedOnUserAccess(student1UserAccess)).isEqualTo(List.of(classroom3));
        verify(repository, times(1)).findAllByStudentsIdsContains(student1UserAccess.id());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return classroom by id")
    void shouldReturnClassroomById() {
        when(repository.findById(classroom1.getId())).thenReturn(Optional.ofNullable(classroom1));

        assertThat(sut.getClassroomById(classroom1.getId())).isEqualTo(classroom1);
        verify(repository, times(1)).findById(classroom1.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should throw exception when classroom doesnt exist")
    void shouldThrowExceptionWhenClassroomDoesntExist() {
        when(repository.findById(classroom1.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(()->sut.getClassroomById(classroom1.getId())).isInstanceOf(EntityNotFoundException.class).hasMessage("Classroom not found");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new classroom")
    void shouldSaveNewClassroom() {
        ClassroomPostDTO classroomPostDTO = new ClassroomPostDTO(classroom2.getName(), classroom2.getDescription(), classroom2.getProfessorId());

        when(repository.save(any(Classroom.class))).thenReturn(classroom2);

        assertThat(sut.save(classroomPostDTO)).isEqualTo(classroom2);
        verify(repository, times(1)).save(any(Classroom.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should edit classroom")
    void shouldEditClassroom() {
        ClassroomPutDTO classroomPutDTO = new ClassroomPutDTO(classroom3.getName(), classroom1.getDescription(), classroom2.getProfessorId());

        when(repository.findById(classroom2.getId())).thenReturn(Optional.ofNullable(classroom2));
        when(repository.save(classroom2)).thenReturn(classroom2);

        Classroom edited = sut.edit(classroom2.getId(), classroomPutDTO);
        assertThat(edited).extracting(Classroom::getName).isEqualTo(classroom3.getName());
        assertThat(edited).extracting(Classroom::getDescription).isEqualTo(classroom1.getDescription());
        assertThat(edited).extracting(Classroom::getProfessorId).isEqualTo(classroom2.getProfessorId());
        verify(repository, times(1)).save(any(Classroom.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to edit if classroom doesnt exist")
    void shouldFailToEditIfClassroomDoesntExist() {
        ClassroomPutDTO classroomPutDTO = new ClassroomPutDTO(classroom3.getName(), classroom1.getDescription(), classroom2.getProfessorId());

        when(repository.findById(classroom2.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(()->sut.edit(classroom2.getId(), classroomPutDTO)).isInstanceOf(EntityNotFoundException.class).hasMessage("Classroom not found");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete classroom")
    void shouldDeleteClassroom() {
        when(sut.existsById(classroom1.getId())).thenReturn(true);
        sut.deleteById(classroom1.getId());
        verify(repository, times(1)).deleteById(classroom1.getId());
        verify(taskService, times(1)).deleteAllByClassroomId(classroom1.getId());
        verify(materialService, times(1)).deleteAllByClassroomId(classroom1.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to delete if classroom doesnt exist")
    void shouldFailToDeleteIfClassroomDoesntExist() {
        when(sut.existsById(classroom1.getId())).thenReturn(false);
        assertThatThrownBy(()->sut.deleteById(classroom1.getId())).isInstanceOf(EntityNotFoundException.class).hasMessage("Classroom not found");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should add student to classroom")
    void shouldAddStudentToClassroom() {
        sut.addStudentToClassroom(student1.getId(), classroom1);

        assertThat(classroom1.containsStudent(student1.getId())).isTrue();
        verify(repository, times(1)).save(any(Classroom.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to add if student is already in the classroom")
    void shouldFailToAddIfStudentIsAlreadyInTheClassroom() {
        classroom1.addStudent(student1.getId());

        assertThatThrownBy(()->sut.addStudentToClassroom(student1.getId(), classroom1)).isInstanceOf(StudentAlreadyInClassroomException.class);
        verify(repository, never()).save(any(Classroom.class));
    }

    @Test
    @DisplayName("should remove student from classroom")
    void shouldRemoveStudentFromClassroom() {
        classroom3.addStudent(student2.getId());

        sut.removeStudentFromClassroom(student2.getId(), classroom3);

        assertThat(classroom3.containsStudent(student2.getId())).isFalse();
        verify(repository, times(1)).save(any(Classroom.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to remove if student is not in the classroom")
    void shouldFailToRemoveIfStudentIsNotInTheClassroom() {
        assertThatThrownBy(()->sut.removeStudentFromClassroom(student1.getId(), classroom1)).isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).save(any(Classroom.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should remove student from all their classrooms")
    void shouldRemoveStudentFromAllTheirClassrooms() {
        classroom1.addStudent(student2.getId());
        classroom2.addStudent(student2.getId());
        classroom3.addStudent(student2.getId());
        classroom1.addStudent(student1.getId());
        classroom3.addStudent(student1.getId());

        sut.removeStudentFromAllClassrooms(student2.getId());
        verify(repository, times(1)).pullStudentFromAllClassrooms(student2.getId());
    }
}
