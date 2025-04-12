package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService sut;

    static Admin admin;
    static Professor professor;
    static Student student;

    @BeforeAll
    static void beforeAll() {
        admin = new Admin("john@mail", "f8dsafksdajf", "John", "Doe");
        professor = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
        student = new Student("john@mail.com", "f8dsafksdajf", "John", "Doe");
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should get user by id")
    void shouldGetUserById() {
        when(repository.findByUUID(admin.getId())).thenReturn(Optional.ofNullable(admin));
        assertThat(sut.getUserById(admin.getId())).isEqualTo(Optional.ofNullable(admin));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty when id doesnt exist")
    void shouldReturnEmptyWhenIdDoesntExist() {
        when(repository.findByUUID(admin.getId())).thenReturn(Optional.empty());
        assertThat(sut.getUserById(admin.getId())).isEqualTo(Optional.empty());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should get user by email")
    void shouldGetUserByEmail() {
        when(repository.findByEmail(admin.getEmail())).thenReturn(Optional.ofNullable(admin));
        assertThat(sut.getUserByEmail(admin.getEmail())).isEqualTo(Optional.ofNullable(admin));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return empty when email doesnt exist")
    void shouldReturnEmptyWhenEmailDoesntExist() {
        when(repository.findByEmail(admin.getEmail())).thenReturn(Optional.empty());
        assertThat(sut.getUserByEmail(admin.getEmail())).isEqualTo(Optional.empty());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should validate user data")
    void shouldValidateUserData() {
        UserSignupDTO userSignupDTO = new UserSignupDTO("johndoe@email.com", "12344567767", "John", "Doe");
        assertThatNoException().isThrownBy(()->sut.validateUserSignupData(userSignupDTO));
    }

    @Tag("UnitTest")
    @ParameterizedTest
    @MethodSource("dataToFailValidation")
    @DisplayName("should fail validation")
    void shouldFailValidation(UserSignupDTO userSignupDTO) {
        assertThatThrownBy(()->sut.validateUserSignupData(userSignupDTO)).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> dataToFailValidation() {
        return Stream.of(
                null,
                Arguments.of(new UserSignupDTO("", "12344567767", "John", "Doe")),
                Arguments.of(new UserSignupDTO("johndoe@email.com", null, "John", "Doe")),
                Arguments.of(new UserSignupDTO("johndoe@email.com", "12344567767", null, "Doe")),
                Arguments.of(new UserSignupDTO("johndoe@email.com", "12344567767   ", "John", "")),
                Arguments.of(new UserSignupDTO(null, "", "", "Doe"))
        );
    }

    @Tag("UnitTest")
    @ParameterizedTest
    @MethodSource("dataToSave")
    @DisplayName("should prepare user data to save")
    void shouldPrepareUserDataToSave(UserSignupDTO userSignupDTO) {
        UserNewDataDTO userNewDataDTO = new UserNewDataDTO("johndoe@email.com", "12344567767", "John", "Doe");

        when(passwordEncoder.encode(anyString())).thenReturn(userNewDataDTO.password());

        assertThat(sut.prepareUserDataToSave(userSignupDTO)).isEqualTo(userNewDataDTO);
    }

    static Stream<Arguments> dataToSave() {
        return Stream.of(
                Arguments.of(new UserSignupDTO("johndoe@email.com", "12344567767", "John", "Doe")),
                Arguments.of(new UserSignupDTO("johndoe@email.com", "12344567767", "  John  ", "Doe")),
                Arguments.of(new UserSignupDTO("johndoe@email.com", "12344567767", "John", "Doe   ")),
                Arguments.of(new UserSignupDTO("     johndoe@email.com", "12344567767", "John", "Doe")),
                Arguments.of(new UserSignupDTO("johndoe@email.com", "12344567767   ", "John", "Doe")),
                Arguments.of(new UserSignupDTO(" johndoe@email.com ", "12344567767  ", "John  ", " Doe"))
        );
    }

    @Tag("UnitTest")
    @ParameterizedTest
    @MethodSource("editUserEmailData")
    @DisplayName("should edit user email")
    void shouldEditUserEmail(User user, UserPutDTO userPutDTO, User editedUser) {
        when(sut.getUserByEmail(anyString())).thenReturn(Optional.empty());
        assertThat(sut.editUserData(user, userPutDTO))
                .extracting("email", "password", "firstName", "lastName")
                .containsExactly(editedUser.getEmail(), editedUser.getPassword(), editedUser.getFirstName(), editedUser.getLastName());
    }

    static Stream<Arguments> editUserEmailData() {
        return Stream.of(
                Arguments.of(
                        admin,
                        new UserPutDTO("johndoe@mail", null, "", null),
                        new Admin(admin.getId(), "johndoe@mail", admin.getPassword(), admin.getFirstName(), admin.getLastName())),
                Arguments.of(
                        student,
                        new UserPutDTO("johndoe@mail.com", "", "Mike", "John"),
                        new Student(student.getId(), "johndoe@mail.com", "f8dsafksdajf", "Mike", "John")));
    }

    @Tag("UnitTest")
    @ParameterizedTest
    @MethodSource("editUserNamesData")
    @DisplayName("should edit user names")
    void shouldEditUserNames(User user, UserPutDTO userPutDTO, User editedUser) {
        assertThat(sut.editUserData(user, userPutDTO))
                .extracting("email", "password", "firstName", "lastName")
                .containsExactly(editedUser.getEmail(), editedUser.getPassword(), editedUser.getFirstName(), editedUser.getLastName());
    }

    static Stream<Arguments> editUserNamesData() {
        return Stream.of(
                Arguments.of(
                        professor,
                        new UserPutDTO(null, "", "Mike", ""),
                        new Professor(professor.getId(), professor.getEmail(), professor.getPassword(), "Mike", professor.getLastName())),
                Arguments.of(
                        student,
                        new UserPutDTO("", "", null, "John"),
                        new Student(student.getId(), student.getEmail(), student.getPassword(), student.getFirstName(), "John")));
    }

    @Tag("UnitTest")
    @ParameterizedTest
    @MethodSource("editUserPasswordData")
    @DisplayName("should edit password")
    void shouldEditUserPassword(User user, UserPutDTO userPutDTO, User editedUser) {
        when(passwordEncoder.encode(anyString())).thenReturn(userPutDTO.password());
        assertThat(sut.editUserData(user, userPutDTO))
                .extracting("email", "password", "firstName", "lastName")
                .containsExactly(editedUser.getEmail(), editedUser.getPassword(), editedUser.getFirstName(), editedUser.getLastName());
    }

    static Stream<Arguments> editUserPasswordData() {
        return Stream.of(
                Arguments.of(
                        admin,
                        new UserPutDTO(null, "f8dsafksdajf", null, null),
                        new Admin(admin.getId(), admin.getEmail(), "f8dsafksdajf", admin.getFirstName(), admin.getLastName())),
                Arguments.of(
                        professor,
                        new UserPutDTO("", "fasdfasdf3fa", "", null),
                        new Professor(professor.getId(), professor.getEmail(), "fasdfasdf3fa", professor.getFirstName(), professor.getLastName())));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should not save if email is already in use")
    void shouldNotSaveIfEmailIsAlreadyInUse() {
        UserPutDTO userPutDTO = new UserPutDTO("johndoe@email.com", "", null, null);

        when(sut.getUserByEmail(anyString())).thenReturn(Optional.of(new Admin("john@mail", "asdfghjk", "John", "Doe")));

        assertThatThrownBy(() -> sut.editUserData(admin, userPutDTO)).isInstanceOf(EmailAlreadyInUseException.class);
    }
}
