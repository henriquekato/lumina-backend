package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteActiveProfessorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.professor.ProfessorRepository;
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
class ProfessorServiceTest {
    @Mock
    ProfessorRepository professorRepository;

    @Mock
    UserService userService;

    @Mock
    ClassroomService classroomService;

    @InjectMocks
    ProfessorService sut;

    static Professor professor;

    @BeforeAll
    static void beforeAll() {
        professor = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return optional professor by id")
    void shouldReturnOptionalProfessorById() {
        when(professorRepository.findById(professor.getId())).thenReturn(Optional.of(professor));
        assertThat(sut.getProfessorById(professor.getId())).isEqualTo(Optional.of(professor));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty when professor id does not exist")
    void shouldReturnEmptyWhenProfessorIdDoesNotExist() {
        when(professorRepository.findById(professor.getId())).thenReturn(Optional.empty());
        assertThat(sut.getProfessorById(professor.getId())).isEqualTo(Optional.empty());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should professor exist")
    void shouldProfessorExist() {
        when(professorRepository.existsById(professor.getId())).thenReturn(true);
        assertThat(sut.existsById(professor.getId())).isEqualTo(true);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should professor not exist")
    void shouldProfessorNotExist() {
        when(professorRepository.existsById(professor.getId())).thenReturn(false);
        assertThat(sut.existsById(professor.getId())).isEqualTo(false);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new professor")
    void shouldSaveNewProfessor() {
        UserSignupDTO userSignupDTO = new UserSignupDTO(professor.getEmail(), professor.getPassword(), professor.getFirstName(), professor.getLastName());
        UserNewDataDTO userNewDataDTO = new UserNewDataDTO(professor.getEmail(), professor.getPassword(), professor.getFirstName(), professor.getLastName());

        when(userService.getUserByEmail(professor.getEmail())).thenReturn(Optional.empty());
        when(userService.prepareUserDataToSave(any(UserSignupDTO.class))).thenReturn(userNewDataDTO);
        when(professorRepository.save(any(Professor.class))).thenReturn(professor);

        assertThat(sut.save(userSignupDTO)).isEqualTo(professor);
        verify(userService, times(1)).getUserByEmail(any());
        verify(userService, times(1)).prepareUserDataToSave(any());
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to save when email is already in use")
    void shouldFailToSaveWhenEmailIsAlreadyInUse() {
        UserSignupDTO userSignupDTO = new UserSignupDTO(professor.getEmail(), professor.getPassword(), professor.getFirstName(), professor.getLastName());

        when(userService.getUserByEmail(professor.getEmail())).thenReturn(Optional.of(professor));

        assertThatThrownBy(() -> sut.save(userSignupDTO)).isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should edit professor")
    void shouldEditProfessor() {
        UserPutDTO userPutDTO = new UserPutDTO("johndoe@mail", null, null, null);
        Professor editedProfessor = new Professor("johndoe@mail", "9fds4hfa", "John", "Doe");

        when(sut.getProfessorById(professor.getId())).thenReturn(Optional.ofNullable(professor));
        when(userService.editUserData(any(Professor.class), any(UserPutDTO.class))).thenReturn(editedProfessor);
        when(professorRepository.save(any(Professor.class))).thenReturn(editedProfessor);

        assertThat(sut.edit(professor.getId(), userPutDTO)).isEqualTo(editedProfessor);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to edit professor when professor does not exist")
    void shouldFailToEditProfessorrWhenProfessorDoesNotExist() {
        UserPutDTO userPutDTO = new UserPutDTO(professor.getFirstName(), professor.getLastName(), professor.getEmail(), professor.getPassword());

        when(sut.getProfessorById(professor.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.edit(professor.getId(), userPutDTO)).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete professor by id")
    void shouldDeleteProfessorById() {
        when(sut.existsById(professor.getId())).thenReturn(true);
        when(classroomService.getClassroomsBasedOnUserAccess(any(UserAccessDTO.class))).thenReturn(List.of());

        sut.deleteById(professor.getId());
        verify(professorRepository, times(1)).deleteById(professor.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to delete professor when professor does not exist")
    void shouldFailToDeleteProfessorWhenProfessorDoesNotExist() {
        when(sut.existsById(professor.getId())).thenReturn(false);
        assertThatThrownBy(() -> sut.deleteById(professor.getId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to delete when professor is active in at least one other classroom")
    void shouldFailToDeleteWhenProfessorIsActiveInAtLeastOneOtherClassroom() {
        when(sut.existsById(professor.getId())).thenReturn(true);
        when(classroomService.getClassroomsBasedOnUserAccess(any(UserAccessDTO.class))).thenReturn(List.of(new Classroom(new ClassroomPostDTO("", "",professor.getId()))));

        assertThatThrownBy(() -> sut.deleteById(professor.getId())).isInstanceOf(CannotDeleteActiveProfessorException.class);
    }
}
