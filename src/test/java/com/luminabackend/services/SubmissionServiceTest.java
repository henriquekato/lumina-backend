package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskCreateDTO;
import com.luminabackend.models.education.task.TaskPutDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Student;
import com.luminabackend.repositories.submission.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
    @Mock
    SubmissionRepository repository;

    @Mock
    FileStorageService fileStorageService;

    @InjectMocks
    SubmissionService sut;

    static Student student1;
    static Student student2;
    static Professor professor1;
    static Professor professor2;
    static Classroom classroom1;
    static Classroom classroom2;
    static Task task1;
    static Task task2;
    static Task task3;
    static Submission submission1;
    static Submission submission2;
    static List<Submission> submissionsTask1;

    @BeforeEach
    void beforeEach() {
        student1 = new Student("jane@mail.com", "23dfa34fga", "Jane", "Doe");
        student2 = new Student("jorge@mail.com", "gfsdgsdfg45", "Jorge", "Doe");
        professor1 = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
        professor2 = new Professor("paulo@mail.com", "fsadfsf3", "Paulo", "Doe");
        classroom1 = new Classroom(new ClassroomPostDTO("Class 1", "Description 1", professor1.getId()));
        classroom2 = new Classroom(new ClassroomPostDTO("Class 2", "Description 2", professor2.getId()));
        task1 = new Task(new TaskCreateDTO("Task 1", "Task", LocalDateTime.now(), classroom1.getId()));
        task2 = new Task(new TaskCreateDTO("Task 2", "Task", LocalDateTime.of(2000, 10, 10, 10, 10, 10), classroom1.getId()));
        task3 = new Task(new TaskCreateDTO("Task 3", "Task", LocalDateTime.now().plusDays(1), classroom2.getId()));
        submission1 = new Submission(new SubmissionPostDTO("Task done"), task1.getId(), student1.getId(), UUID.randomUUID().toString());
        submission2 = new Submission(new SubmissionPostDTO("Task completed"), task1.getId(), student2.getId(), UUID.randomUUID().toString());
        submissionsTask1 = List.of(submission1, submission2);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return all submissions from one task")
    void shouldReturnAllSubmissionsOneTask() {
        when(repository.findAllByTaskId(task1.getId())).thenReturn(submissionsTask1);

        List<Submission> submissions = sut.getAllSubmissionsByTaskId(task1.getId());

        assertThat(submissions).isEqualTo(submissionsTask1);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return submission by id")
    void shouldReturnSubmissionById() {
        when(repository.findById(submission1.getId())).thenReturn(Optional.ofNullable(submission1));

        Submission actual = sut.getSubmissionById(submission1.getId());

        assertThat(actual).isEqualTo(submission1);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should throw exception when submission doesnt exist")
    void shouldThrowExceptionWhenSubmissionDoesntExist() {
        when(repository.findById(submission1.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.getSubmissionById(submission1.getId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new submission")
    void shouldSaveNewSubmission() throws IOException {
        when(repository.existsByStudentIdAndTaskId(student1.getId(), task1.getId())).thenReturn(false);
        when(repository.save(any(Submission.class))).thenReturn(submission2);

        Path path = Paths.get("src/test/resources/input.txt");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile multipartFile = new MockMultipartFile("input", "input.txt", "text/plain", content);

        sut.save(submission1.getTaskId(), submission1.getStudentId(), new SubmissionPostDTO("Content submitted"), multipartFile);

        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), any(UUID.class));
        verify(repository, times(1)).save(any(Submission.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should fail to save if task has already received a submission")
    void shouldFailToSaveIfTaskHasAlreadyReceivedASubmission() throws IOException {
        when(repository.existsByStudentIdAndTaskId(student1.getId(), task1.getId())).thenReturn(true);

        Path path = Paths.get("src/test/resources/input.txt");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile multipartFile = new MockMultipartFile("input", "input.txt", "text/plain", content);

        assertThatThrownBy(()->sut.save(submission1.getTaskId(), submission1.getStudentId(), new SubmissionPostDTO("Content submitted"), multipartFile));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete submission and its file")
    void shouldDeleteSubmissionAndItsFile() {
        when(repository.findById(submission1.getId())).thenReturn(Optional.ofNullable(submission1));
        sut.deleteById(submission1.getId());

        verify(fileStorageService, times(1)).deleteFile(anyString());
        verify(repository, times(1)).deleteById(submission1.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete all submissions by task id")
    void shouldDeleteAllSubmissionsByTaskId() {
        when(repository.findAllByTaskId(task1.getId())).thenReturn(submissionsTask1);

        sut.deleteAllByTaskId(task1.getId());

        verify(fileStorageService, times(submissionsTask1.size())).deleteFile(anyString());
        verify(repository, times(submissionsTask1.size())).delete(any(Submission.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should update grade of submission")
    void shouldUpdateGradeOfSubmission() {
        when(repository.findById(submission1.getId())).thenReturn(Optional.ofNullable(submission1));

        sut.submissionAssessment(submission1.getId(), new SubmissionAssessmentDTO(5.0));
        assertThat(submission1).extracting(Submission::getGrade).isEqualTo(5.0);
        verify(repository, times(1)).save(any(Submission.class));
    }
}
