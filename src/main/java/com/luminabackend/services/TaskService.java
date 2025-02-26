package com.luminabackend.services;

import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskPostDTO;
import com.luminabackend.repositories.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    public Task save(TaskPostDTO taskPostDTO) {
        Task task = new Task(taskPostDTO);
        task.setTitle(task.getTitle().trim());
        task.setDescription(task.getDescription().trim());
        return repository.save(task);
    }

    public Task save(Task task) {
        return repository.save(task);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<Task> getAllTasks(UUID classroomId) {
        return repository.findAll().stream().filter(t -> t.getClassroomId().equals(classroomId)).toList();
    }

    public Optional<Task> getTaskById(UUID id) {
        return repository.findById(id);
    }

    public void deleteAll(UUID classroomId){
        List<Task> classroomTasks = repository
                                    .findAll()
                                    .stream()
                                    .filter(c -> c.getClassroomId().equals(classroomId)).toList();
        repository.deleteAllById(classroomTasks.stream().map(Task::getId).toList());
    }
}
