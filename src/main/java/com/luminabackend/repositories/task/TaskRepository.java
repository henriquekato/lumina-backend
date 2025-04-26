package com.luminabackend.repositories.task;

import com.luminabackend.models.education.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
    List<Task> findAllByClassroomIdOrderByDueDateAsc(UUID classroomId);
    Page<Task> findAllByClassroomIdOrderByDueDateAsc(UUID classroomId, Pageable page);
    List<Task> findAllByDueDateAfterOrderByDueDateAsc(LocalDateTime date);
    List<Task> findAllByClassroomIdInAndDueDateAfterOrderByDueDateAsc(List<UUID> classroomIds, LocalDateTime date);
    List<Task> findAllByClassroomIdInOrderByDueDateDesc(List<UUID> classroomsIds);
}
