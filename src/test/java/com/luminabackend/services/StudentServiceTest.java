package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.student.StudentRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    StudentRepository studentRepository;

    @Mock
    UserService userService;

    @Mock
    ClassroomService classroomService;

    @InjectMocks
    StudentService sut;

    static Student student;

    @BeforeAll
    static void beforeAll() {
        student = new Student("john@mail.com", "9fds4hfa", "John", "Doe");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return all students")
    void shouldReturnAllStudents() {
        List<Student> students = List.of(
                new Student("john@mail.com", "9fds4hfa", "John", "Doe"),
                new Student("jane@mail.com", "4fas3fa", "Jane", "Doe"),
                new Student("mike@mail.com", "va34fa324", "Mike", "Doe"));
        when(studentRepository.findAll()).thenReturn(students);
        assertThat(sut.getAllStudents()).isEqualTo(students);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty list when studentRepository returns empty list")
    void shouldReturnEmptyList() {
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(sut.getAllStudents()).isEqualTo(Collections.emptyList());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return optional student by id")
    void shouldReturnOptionalStudentById() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        assertThat(sut.getStudentById(student.getId())).isEqualTo(Optional.of(student));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty when student id does not exist")
    void shouldReturnEmptyWhenStudentIdDoesNotExist() {
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());
        assertThat(sut.getStudentById(student.getId())).isEqualTo(Optional.empty());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should student exist")
    void shouldStudentExist() {
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        assertThat(sut.existsById(student.getId())).isEqualTo(true);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should student not exist")
    void shouldStudentNotExist() {
        when(studentRepository.existsById(student.getId())).thenReturn(false);
        assertThat(sut.existsById(student.getId())).isEqualTo(false);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new student")
    void shouldSaveNewStudent() {
        UserSignupDTO userSignupDTO = new UserSignupDTO(student.getEmail(), student.getPassword(), student.getFirstName(), student.getLastName());
        UserNewDataDTO userNewDataDTO = new UserNewDataDTO(student.getEmail(), student.getPassword(), student.getFirstName(), student.getLastName());

        when(userService.getUserByEmail(student.getEmail())).thenReturn(Optional.empty());
        when(userService.prepareUserDataToSave(any(UserSignupDTO.class))).thenReturn(userNewDataDTO);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        assertThat(sut.save(userSignupDTO)).isEqualTo(student);
        verify(userService, times(1)).getUserByEmail(any());
        verify(userService, times(1)).prepareUserDataToSave(any());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to save when email is already in use")
    void shouldFailToSaveWhenEmailIsAlreadyInUse() {
        UserSignupDTO userSignupDTO = new UserSignupDTO(student.getEmail(), student.getPassword(), student.getFirstName(), student.getLastName());

        when(userService.getUserByEmail(student.getEmail())).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> sut.save(userSignupDTO)).isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should edit student")
    void shouldEditStudent() {
        UserPutDTO userPutDTO = new UserPutDTO("johndoe@mail", null, null, null);
        Student editedStudent = new Student("johndoe@mail", "9fds4hfa", "John", "Doe");

        when(sut.getStudentById(student.getId())).thenReturn(Optional.ofNullable(student));
        when(userService.editUserData(any(Student.class), any(UserPutDTO.class))).thenReturn(editedStudent);
        when(studentRepository.save(any(Student.class))).thenReturn(editedStudent);

        assertThat(sut.edit(student.getId(), userPutDTO)).isEqualTo(editedStudent);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to edit student when student does not exist")
    void shouldFailToEditStudentrWhenStudentDoesNotExist() {
        UserPutDTO userPutDTO = new UserPutDTO(student.getFirstName(), student.getLastName(), student.getEmail(), student.getPassword());

        when(sut.getStudentById(student.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.edit(student.getId(), userPutDTO)).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete student by id")
    void shouldDeleteStudentById() {
        when(sut.existsById(student.getId())).thenReturn(true);

        sut.deleteById(student.getId());
        verify(studentRepository, times(1)).deleteById(student.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to delete student when student does not exist")
    void shouldFailToDeleteStudentWhenStudentDoesNotExist() {
        when(sut.existsById(student.getId())).thenReturn(false);
        assertThatThrownBy(() -> sut.deleteById(student.getId())).isInstanceOf(EntityNotFoundException.class);
    }
}
