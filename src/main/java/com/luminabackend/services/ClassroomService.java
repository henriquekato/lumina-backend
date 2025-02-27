package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.StudentAlreadyInClassroomException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.classroom.ClassroomPutDTO;
import com.luminabackend.models.user.Role;
import com.luminabackend.repositories.classroom.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository repository;

    @Autowired
    private TaskService taskService;

    public List<Classroom> getFilteredClassrooms(Role role, UUID userId) {
        if (role.equals(Role.ADMIN)) {
            return repository.findAll();
        }
        else if (role.equals(Role.PROFESSOR)) {
            return repository.findAllByProfessorId(userId);
        }
        return repository.findAllByStudentsIdsContains(userId);
    }

    public Optional<Classroom> getClassroomById(UUID id) {
        return repository.findById(id);
    }

    public Classroom getClassroomBasedOnUserPermission(UUID classroomId, Role role, UUID userId) {
        Optional<Classroom> classroomById = repository.findById(classroomId);

        if (classroomById.isEmpty()) throw new EntityNotFoundException("Classroom not found");

        Classroom classroom = classroomById.get();

        if (role.equals(Role.ADMIN)) return classroom;

        if (classroom.getProfessorId().equals(userId)) return classroom;

        if (studentInClassroom(classroom, userId)) return classroom;

        throw new AccessDeniedException("You don't have permission to access this resource");
    }

    public Optional<Classroom> getClassroomByName(String name) {
        return repository.findByName(name);
    }

    public boolean existsClassroomById(UUID id) {
        return repository.existsById(id);
    }

    public Classroom save(ClassroomPostDTO classroomPostDTO) {
        Classroom classroom = new Classroom(classroomPostDTO);

        classroom.setName(classroom.getName().trim());
        if (classroomPostDTO.description() != null) classroom.setDescription(classroomPostDTO.description().trim());

        return repository.save(classroom);
    }

    public Classroom edit(UUID id, ClassroomPutDTO classroomPutDTO) {
        Optional<Classroom> classroomById = getClassroomById(id);
        if (classroomById.isEmpty()) throw new EntityNotFoundException("Classroom not found");

        Classroom classroom = classroomById.get();

        if (classroomPutDTO.name() != null) {
            classroom.setName(classroomPutDTO.name().trim());
        }
        if (classroomPutDTO.description() != null) {
            classroom.setDescription(classroomPutDTO.description().trim());
        }

        return repository.save(classroom);
    }

    public void deleteClassroomById(UUID id) {
        if (!existsClassroomById(id)) {
            throw new EntityNotFoundException("Classroom not found");
        }

        taskService.deleteAll(id);
        // materials

        repository.deleteById(id);
    }

    private boolean studentInClassroom(Classroom classroom, UUID studentId) {
        return classroom.getStudentsIds().contains(studentId);
    }

    public Classroom addStudentToClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId).orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        if (studentInClassroom(classroom, studentId))
            throw new StudentAlreadyInClassroomException("The student you are trying to add is already in this class");

        classroom.addStudent(studentId);
        return repository.save(classroom);
    }

    public void removeStudentFromClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId).orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        if (studentInClassroom(classroom, studentId))
            throw new EntityNotFoundException("The student you are trying to remove is not in this class");

        classroom.removeStudent(studentId);

        repository.save(classroom);
    }
}
