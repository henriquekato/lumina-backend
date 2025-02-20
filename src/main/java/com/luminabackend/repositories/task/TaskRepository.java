package com.luminabackend.repositories.task;

import com.luminabackend.models.education.task.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
}
