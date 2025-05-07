package com.luminabackend.repositories.task;

import com.luminabackend.models.education.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends MongoRepository<Task, UUID> {
    List<Task> findAllByClassroomIdOrderByDueDateAsc(UUID classroomId);
    Page<Task> findAllByClassroomIdOrderByDueDateAsc(UUID classroomId, Pageable page);

    @Aggregation(pipeline = {
        "{$match:  {'dueDate': {$gt: ?0}}}",
        "{$lookup: {from:  'classrooms', localField:  'classroomId', foreignField:  '_id', as:  'allClassrooms'}}",
        "{$unwind: '$allClassrooms'}",
        "{$addFields: {'classroomName': '$allClassrooms.name'}}",
        "{$project: {'allClassrooms': 0}}",
        "{$sort:  {'dueDate':  1}}"
    })
    List<Task> findAllAfterDueDate(LocalDateTime dateTime);
}
