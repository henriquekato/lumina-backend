package com.luminabackend.repositories.user;

import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Page<User> findAllByRoleIn(List<Role> roles, Pageable page);
    int countUserByRoleIs(Role role);

    @Aggregation(pipeline = {
            "{$match:  {'_id':  ?0}}",
            "{$lookup: {from:  'classrooms', localField:  '_id', foreignField:  'professorId', as:  'professorClassrooms'}}",
            "{$unwind: '$professorClassrooms'}",
            "{$lookup:  {from:  'tasks', localField:  'professorClassrooms._id', foreignField:  'classroomId', as:  'professorTasks'}}",
            "{$unwind: '$professorTasks'}",
            "{$match:  {'professorTasks.dueDate': {$gt: ?1}}}",
            "{$addFields: {_id: '$professorTasks._id', title: '$professorTasks.title', description: '$professorTasks.description', dueDate: '$professorTasks.dueDate', classroomId: '$professorClassrooms._id', classroomName: '$professorClassrooms.name'}}",
            "{$project: {'professorClassrooms': 0, 'professorTasks': 0}}",
            "{$sort:  {'dueDate':  1}}"
    })
    List<Task> findProfessorTasks(UUID professorId, LocalDateTime dateTime);

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
