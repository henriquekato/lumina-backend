package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.TaskDueDateExpiredException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskCreateDTO;
import com.luminabackend.models.education.task.TaskPutDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.repositories.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    TaskRepository repository;

    @Mock
    SubmissionService submissionService;

    @InjectMocks
    TaskService sut;

    static Professor professor1;
    static Professor professor2;
    static Classroom classroom1;
    static Classroom classroom2;
    static Task task1;
    static Task task2;
    static Task task3;
    static List<Task> tasksClassroom1;

    @BeforeEach
    void beforeEach() {
        professor1 = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
        professor2 = new Professor("paulo@mail.com", "fsadfsf3", "Paulo", "Doe");
        classroom1 = new Classroom(new ClassroomPostDTO("Class 1", "Description 1", professor1.getId()));
        classroom2 = new Classroom(new ClassroomPostDTO("Class 2", "Description 2", professor2.getId()));
        task1 = new Task(new TaskCreateDTO("Task 1", "Task", LocalDateTime.now(), classroom1.getId()));
        task2 = new Task(new TaskCreateDTO("Task 2", "Task", LocalDateTime.of(2000, 10, 10, 10, 10, 10), classroom1.getId()));
        task3 = new Task(new TaskCreateDTO("Task 3", "Task", LocalDateTime.now().plusDays(1), classroom2.getId()));
        tasksClassroom1 = List.of(task1, task2);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return all tasks from one classroom")
    void shouldReturnAllTasksOneClassroom() {
        when(repository.findAllByClassroomId(classroom1.getId())).thenReturn(tasksClassroom1);

        List<Task> tasks = sut.getAllTasksByClassroomId(classroom1.getId());

        assertThat(tasks).isEqualTo(tasksClassroom1);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return task by id")
    void shouldReturnTaskById() {
        when(repository.findById(task2.getId())).thenReturn(Optional.ofNullable(task2));

        Task actual = sut.getTaskById(task2.getId());

        assertThat(actual).isEqualTo(task2);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should throw exception when task doesnt exist")
    void shouldThrowExceptionWhenTaskDoesntExist() {
        when(repository.findById(task2.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.getTaskById(task2.getId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new task")
    void shouldSaveNewTask() {
        when(repository.save(any(Task.class))).thenReturn(task3);

        sut.save(new TaskCreateDTO("Task 1", "Task", LocalDateTime.now(), classroom1.getId()));

        verify(repository, times(1)).save(any(Task.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should edit task")
    void shouldEditTask() {
        when(repository.findById(task1.getId())).thenReturn(Optional.ofNullable(task1));
        when(repository.save(any(Task.class))).thenReturn(task1);

        sut.edit(task1.getId(), new TaskPutDTO("Title", "Task", LocalDateTime.now()));

        verify(repository, times(1)).save(any(Task.class));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete task")
    void shouldDeleteTask() {
        when(repository.existsById(task2.getId())).thenReturn(true);
        sut.deleteById(task2.getId());

        verify(submissionService, times(1)).deleteAllByTaskId(task2.getId());
        verify(repository, times(1)).deleteById(task2.getId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete all tasks by classroom id")
    void shouldDeleteAllTasksByClassroomId() {
        when(repository.findAllByClassroomId(task1.getClassroomId())).thenReturn(tasksClassroom1);

        sut.deleteAllByClassroomId(classroom1.getId());

        verify(repository, times(1)).findAllByClassroomId(task2.getClassroomId());
        verify(submissionService, times(tasksClassroom1.size())).deleteAllByTaskId(any(UUID.class));
        verify(repository, times(1)).deleteAllById(anyCollection());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should due date not be expired")
    void shouldDueDateNotBeExpired() {
        when(repository.findById(task3.getId())).thenReturn(Optional.of(task3));

        assertThatNoException().isThrownBy(()->sut.isDueDateExpired(task3.getId()));
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should throw exception if due date is expired")
    void shouldThrowExceptionIfDueDateIsExpired() {
        when(repository.findById(task2.getId())).thenReturn(Optional.of(task2));

        assertThatThrownBy(()->sut.isDueDateExpired(task2.getId())).isInstanceOf(TaskDueDateExpiredException.class);
    }
}
