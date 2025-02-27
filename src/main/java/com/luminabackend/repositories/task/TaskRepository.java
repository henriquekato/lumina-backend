package com.luminabackend.repositories.task;

import com.luminabackend.models.education.task.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
    List<Task> findByClassroomId(UUID classroomId);
}
