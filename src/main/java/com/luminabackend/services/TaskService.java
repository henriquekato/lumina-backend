package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskCreateDTO;
import com.luminabackend.models.education.task.TaskPutDTO;
import com.luminabackend.repositories.task.TaskRepository;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private SubmissionService submissionService;
    
    @Autowired
    private PermissionService permissionService;

    public List<Task> getAllTasks(UUID classroomId, PayloadDTO payloadDTO) {
        permissionService.checkAccessToClassroom(classroomId, payloadDTO);
        return repository.findAllByClassroomId(classroomId);
    }

    public Page<Task> getPaginatedClassroomTasks(UUID classroomId, PayloadDTO payloadDTO, Pageable page) {
        permissionService.checkAccessToClassroom(classroomId, payloadDTO);
        return repository.findAllByClassroomId(classroomId, page);
    }

    public Task getTaskById(UUID id) {
        Optional<Task> taskById = repository.findById(id);
        if (taskById.isEmpty()) throw new EntityNotFoundException("Task not found");
        return taskById.get();
    }
    
    public Task getTaskBasedOnUserPermission(UUID taskId, UUID classroomId, PayloadDTO payloadDTO){
        permissionService.checkAccessToClassroom(classroomId, payloadDTO);
        return getTaskById(taskId);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Task save(UUID classroomId, PayloadDTO payloadDTO, TaskCreateDTO taskCreateDTO) {
        permissionService.checkAccessToClassroom(classroomId, payloadDTO);

        Task task = new Task(taskCreateDTO);
        task.setTitle(task.getTitle().trim());
        task.setDescription(task.getDescription().trim());
        return repository.save(task);
    }

    public Task edit(UUID taskId, UUID classroomId, PayloadDTO payloadDTO, TaskPutDTO taskPutDTO) {
        permissionService.checkAccessToClassroom(classroomId, payloadDTO);

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

    public void deleteById(UUID taskId, UUID classroomId, PayloadDTO payloadDTO) {
        permissionService.checkAccessToClassroom(classroomId, payloadDTO);

        if (!existsById(taskId)) {
            throw new EntityNotFoundException("Task not found");
        }

        submissionService.deleteAllByTaskId(taskId);
        repository.deleteById(taskId);
    }

    public void deleteAllByClassroomId(UUID classroomId){
        List<Task> classroomTasks = repository.findAllByClassroomId(classroomId);
        classroomTasks.forEach(task -> submissionService.deleteAllByTaskId(task.getId()));
        repository.deleteAllById(classroomTasks.stream().map(Task::getId).toList());
    }
}
