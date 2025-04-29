package com.luminabackend.repositories.professor;

import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Professor;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessorRepository extends MongoRepository<Professor, UUID> {
    Optional<Professor> findByEmail(String email);

    @Aggregation(pipeline = {
        "{$match:  {'_id':  ?0}}",
        "{$lookup: {from:  'classrooms', localField:  '_id', foreignField:  'professorId', as:  'professorClassrooms'}}",
        "{$unwind: '$professorClassrooms'}",
        "{$lookup:  {from:  'tasks', localField:  'professorClassrooms._id', foreignField:  'classroomId', as:  'professorTasks'}}",
        "{$unwind: '$professorTasks'}",
        "{$addFields: {_id: '$professorTasks._id', title: '$professorTasks.title', description: '$professorTasks.description', dueDate: '$professorTasks.dueDate', classroomId: '$professorClassrooms._id', classroomName: '$professorClassrooms.name'}}",
        "{$project: {'professorClassrooms': 0, 'professorTasks': 0}}",
        "{$match:  {'dueDate': {$gt: ?1}}}",
        "{$sort:  {'dueDate':  1}}"
    })
    List<Task> findProfessorTasks(UUID professorId, LocalDateTime dateTime);
}
