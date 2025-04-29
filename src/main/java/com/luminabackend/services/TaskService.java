package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.TaskDueDateExpiredException;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskCreateDTO;
import com.luminabackend.models.education.task.TaskPutDTO;
import com.luminabackend.repositories.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private SubmissionService submissionService;

    public List<Task> getAllTasksByClassroomId(UUID classroomId) {
        return repository.findAllByClassroomIdOrderByDueDateAsc(classroomId);
    }

    public Page<Task> getPaginatedClassroomTasks(UUID classroomId, Pageable page) {
        return repository.findAllByClassroomIdOrderByDueDateAsc(classroomId, page);
    }

    public Task getTaskById(UUID taskId) {
        Optional<Task> taskById = repository.findById(taskId);
        if (taskById.isEmpty()) throw new EntityNotFoundException("Task not found");
        return taskById.get();
    }

    public boolean existsById(UUID taskId) {
        return repository.existsById(taskId);
    }

    public void checkTaskExistenceById(UUID taskId) {
        if (!existsById(taskId)) {
            throw new EntityNotFoundException("Task not found");
        }
    }

    public Task save(TaskCreateDTO taskCreateDTO) {
        Task task = new Task(taskCreateDTO);
        task.setTitle(task.getTitle().trim());
        task.setDescription(task.getDescription().trim());
        return repository.save(task);
    }

    public Task edit(UUID taskId, TaskPutDTO taskPutDTO) {
        Task task = getTaskById(taskId);
        if (taskPutDTO.title() != null) {
            task.setTitle(taskPutDTO.title().trim());
        }
        if (taskPutDTO.description() != null) {
            task.setDescription(taskPutDTO.description().trim());
        }
        if (taskPutDTO.dueDate() != null) {
            task.setDueDate(taskPutDTO.dueDate());
        }

        return repository.save(task);
    }

    @Transactional
    public void deleteById(UUID taskId) {
        checkTaskExistenceById(taskId);

        submissionService.deleteAllByTaskId(taskId);
        repository.deleteById(taskId);
    }

    @Transactional
    public void deleteAllByClassroomId(UUID classroomId) {
        List<Task> classroomTasks = repository.findAllByClassroomIdOrderByDueDateAsc(classroomId);
        classroomTasks.forEach(task -> submissionService.deleteAllByTaskId(task.getId()));
        repository.deleteAllById(classroomTasks.stream().map(Task::getId).toList());
    }

    public void isDueDateExpired(UUID taskId) {
        Task task = getTaskById(taskId);
        if (task.isDueDateExpired())
            throw new TaskDueDateExpiredException("Task due date expired");
    }

    public Page<Task> getAllTasks(Pageable page){
        List<Task> tasks = repository.findAllAfterDueDate(LocalDateTime.now());
        final int start = (int) page.getOffset();
        final int end = Math.min((start + page.getPageSize()), tasks.size());
        List<Task> subList = List.of();
        if (start <= end) subList = tasks.subList(start, end);
        return new PageImpl<>(subList, page, subList.size());
    }
}
