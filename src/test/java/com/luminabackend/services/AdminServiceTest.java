package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteLastAdministratorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.admin.AdminRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    AdminRepository adminRepository;

    @Mock
    UserService userService;

    @InjectMocks
    AdminService sut;

    static Admin admin;

    @BeforeAll
    static void beforeAll() {
        admin = new Admin("john@mail.com", "9fds4hfa", "John", "Doe");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return all admins")
    void shouldReturnAllAdmins() {
        List<Admin> admins = List.of(
                new Admin("john@mail.com", "9fds4hfa", "John", "Doe"),
                new Admin("jane@mail.com", "4fas3fa", "Jane", "Doe"),
                new Admin("mike@mail.com", "va34fa324", "Mike", "Doe"));
        when(adminRepository.findAll()).thenReturn(admins);
        assertThat(sut.getAllAdmins()).isEqualTo(admins);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty list when adminRepository returns empty list")
    void shouldReturnEmptyList() {
        when(adminRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(sut.getAllAdmins()).isEqualTo(Collections.emptyList());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return optional admin by id")
    void shouldReturnOptionalAdminById() {
        when(adminRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        assertThat(sut.getAdminById(admin.getId())).isEqualTo(Optional.of(admin));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty when admin id does not exist")
    void shouldReturnEmptyWhenAdminIdDoesNotExist() {
        when(adminRepository.findById(admin.getId())).thenReturn(Optional.empty());
        assertThat(sut.getAdminById(admin.getId())).isEqualTo(Optional.empty());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return optional admin by email")
    void shouldReturnOptionalAdminByEmail() {
        when(adminRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        assertThat(sut.getAdminByEmail(admin.getEmail())).isEqualTo(Optional.of(admin));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty when admin email does not exist")
    void shouldReturnEmptyWhenAdminEmailDoesNotExist() {
        when(adminRepository.findByEmail(admin.getEmail())).thenReturn(Optional.empty());
        assertThat(sut.getAdminByEmail(admin.getEmail())).isEqualTo(Optional.empty());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should admin exist")
    void shouldAdminExist() {
        when(adminRepository.existsById(admin.getId())).thenReturn(true);
        assertThat(sut.existsById(admin.getId())).isEqualTo(true);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should admin not exist")
    void shouldAdminNotExist() {
        when(adminRepository.existsById(admin.getId())).thenReturn(false);
        assertThat(sut.existsById(admin.getId())).isEqualTo(false);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new admin")
    void shouldSaveNewAdmin() {
        UserSignupDTO userSignupDTO = new UserSignupDTO(admin.getEmail(), admin.getPassword(), admin.getFirstName(), admin.getLastName());
        UserNewDataDTO userNewDataDTO = new UserNewDataDTO(admin.getEmail(), admin.getPassword(), admin.getFirstName(), admin.getLastName());

        when(userService.getUserByEmail(admin.getEmail())).thenReturn(Optional.empty());
        when(userService.prepareUserDataToSave(any(UserSignupDTO.class))).thenReturn(userNewDataDTO);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        assertThat(sut.save(userSignupDTO)).isEqualTo(admin);
        verify(userService, times(1)).getUserByEmail(any());
        verify(userService, times(1)).prepareUserDataToSave(any());
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to save when email is already in use")
    void shouldFailToSaveWhenEmailIsAlreadyInUse() {
        UserSignupDTO userSignupDTO = new UserSignupDTO(admin.getEmail(), admin.getPassword(), admin.getFirstName(), admin.getLastName());

        when(userService.getUserByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> sut.save(userSignupDTO)).isInstanceOf(EmailAlreadyInUseException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should edit admin")
    void shouldEditAdmin() {
        UserPutDTO userPutDTO = new UserPutDTO("johndoe@mail", null, null, null);
        Admin editedAdmin = new Admin("johndoe@mail", "9fds4hfa", "John", "Doe");

        when(sut.getAdminById(admin.getId())).thenReturn(Optional.ofNullable(admin));
        when(userService.editUserData(any(Admin.class), any(UserPutDTO.class))).thenReturn(editedAdmin);
        when(adminRepository.save(any(Admin.class))).thenReturn(editedAdmin);

        assertThat(sut.edit(admin.getId(), userPutDTO)).isEqualTo(editedAdmin);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to edit admin when admin does not exist")
    void shouldFailToEditAdminWhenAdminDoesNotExist() {
        UserPutDTO userPutDTO = new UserPutDTO(admin.getFirstName(), admin.getLastName(), admin.getEmail(), admin.getPassword());

        when(sut.getAdminById(admin.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.edit(admin.getId(), userPutDTO)).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete admin by id")
    void shouldDeleteAdminById() {
        when(sut.existsById(admin.getId())).thenReturn(true);
        when(sut.count()).thenReturn(2L);

        sut.deleteById(admin.getId());
        verify(adminRepository, times(1)).deleteById(admin.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to delete admin when admin does not exist")
    void shouldFailToDeleteAdminWhenAdminDoesNotExist() {
        when(sut.existsById(admin.getId())).thenReturn(false);
        assertThatThrownBy(() -> sut.deleteById(admin.getId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to delete when admin is the last admin")
    void shouldFailToDeleteWhenAdminIsTheLastAdmin() {
        when(sut.existsById(admin.getId())).thenReturn(true);
        when(adminRepository.count()).thenReturn(1L);

        assertThatThrownBy(() -> sut.deleteById(admin.getId())).isInstanceOf(CannotDeleteLastAdministratorException.class);
    }
}
