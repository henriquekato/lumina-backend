package com.luminabackend.services;

import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskPostDTO;
import com.luminabackend.models.user.student.Student;
import com.luminabackend.models.user.student.StudentPostDTO;
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
        return repository.save(task);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    public Optional<Task> getTaskById(UUID id) {
        return repository.findById(id);
    }
}
