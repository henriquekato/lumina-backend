package com.luminabackend.repositories.student;

import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Student;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends MongoRepository<Student, UUID> {
    Optional<Student> findByEmail(String email);

    @Aggregation(pipeline = {
            "{$match:  {'_id':  ?0}}",
            "{$lookup: {from:  'classrooms', localField:  '_id', foreignField:  'studentsIds', as:  'studentClassrooms'}}",
            "{$unwind: '$studentClassrooms'}",
            "{$lookup:  {from:  'tasks', localField:  'studentClassrooms._id', foreignField:  'classroomId', as:  'studentTasks'}}",
            "{$unwind: '$studentTasks'}",
            "{$lookup:  {from:  'submissions', localField:  'studentTasks._id', foreignField:  'taskId', as:  'studentSubmissions'}}",
            "{$unwind: '$studentSubmissions'}",
            "{$addFields: {_id: '$studentTasks._id', title: '$studentTasks.title', description: '$studentTasks.description', dueDate: '$studentTasks.dueDate', classroomId: '$studentClassrooms._id', classroomName: '$studentClassrooms.name'}}",
            "{$project: {'studentClassrooms': 0, 'studentTasks': 0, 'studentSubmissions':  0}}",
            "{$sort:  {'dueDate':  -1}}"
    })
    List<Task> findStudentDoneTasks(UUID studentId);

    @Aggregation(pipeline = {
            "{$match:  {'_id':  ?0}}",
            "{$lookup: {from:  'classrooms', localField:  '_id', foreignField:  'studentsIds', as:  'studentClassrooms'}}",
            "{$unwind: '$studentClassrooms'}",
            "{$lookup:  {from:  'tasks', localField:  'studentClassrooms._id', foreignField:  'classroomId', as:  'studentTasks'}}",
            "{$unwind: '$studentTasks'}",
            "{$lookup:  {from:  'submissions', localField:  'studentTasks._id', foreignField:  'taskId', as:  'studentSubmissions'}}",
            "{$match: {$expr: {$and: [{$gt: ['$studentTasks.dueDate', ?1]}, {$eq: [{$size: '$studentSubmissions'}, 0]}]}}}",
            "{$addFields: {_id: '$studentTasks._id', title: '$studentTasks.title', description: '$studentTasks.description', dueDate: '$studentTasks.dueDate', classroomId: '$studentClassrooms._id', classroomName: '$studentClassrooms.name'}}",
            "{$project: {'studentClassrooms': 0, 'studentTasks': 0, 'studentSubmissions':  0}}",
            "{$sort:  {'dueDate':  1}}"
    })
    List<Task> findStudentNotDoneTasks(UUID studentId, LocalDateTime dateTime);
}
