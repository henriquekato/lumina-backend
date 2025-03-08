package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.StudentAlreadyInClassroomException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.classroom.ClassroomPutDTO;
import com.luminabackend.models.user.Role;
import com.luminabackend.repositories.classroom.ClassroomRepository;
import com.luminabackend.utils.security.PayloadDTO;
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

    @Autowired
    private PermissionService permissionService;

    public List<Classroom> getFilteredClassrooms(Role role, UUID userId) {
        if (role.equals(Role.ADMIN))
            return repository.findAll();

        if (role.equals(Role.PROFESSOR))
            return repository.findAllByProfessorId(userId);

        return repository.findAllByStudentsIdsContains(userId);
    }

    public Classroom getClassroomById(UUID id) {
        Optional<Classroom> classroomById = repository.findById(id);
        if (classroomById.isEmpty()) throw new EntityNotFoundException("Classroom not found");
        return classroomById.get();
    }

    public Classroom getClassroomBasedOnUserPermission(UUID classroomId, PayloadDTO payloadDTO) {
        Classroom classroom = getClassroomById(classroomId);
        permissionService.checkAccessToClassroom(payloadDTO, classroom);
        return classroom;
    }

    public Optional<Classroom> getClassroomByName(String name) {
        return repository.findByName(name);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Classroom save(ClassroomPostDTO classroomPostDTO) {
        Classroom classroom = new Classroom(classroomPostDTO);

        classroom.setName(classroom.getName().trim());
        if (classroomPostDTO.description() != null) classroom.setDescription(classroomPostDTO.description().trim());

        return repository.save(classroom);
    }

    public Classroom edit(UUID id, ClassroomPutDTO classroomPutDTO) {
        Classroom classroom = getClassroomById(id);

        if (classroomPutDTO.name() != null) {
            classroom.setName(classroomPutDTO.name().trim());
        }
        if (classroomPutDTO.description() != null) {
            classroom.setDescription(classroomPutDTO.description().trim());
        }

        return repository.save(classroom);
    }

    public void deleteById(UUID id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Classroom not found");
        }

        taskService.deleteAllByClassroomId(id);

        // materials

        repository.deleteById(id);
    }

    public Classroom addStudentToClassroom(UUID studentId, UUID classroomId, PayloadDTO payloadDTO) {
        Classroom classroom = getClassroomBasedOnUserPermission(classroomId, payloadDTO);

        if (classroom.containsStudent(studentId))
            throw new StudentAlreadyInClassroomException("The student you are trying to add is already in this class");

        classroom.addStudent(studentId);
        return repository.save(classroom);
    }

    public void removeStudentFromClassroom(UUID studentId, UUID classroomId, PayloadDTO payloadDTO) {
        Classroom classroom = getClassroomBasedOnUserPermission(classroomId, payloadDTO);

        if (!classroom.containsStudent(studentId))
            throw new EntityNotFoundException("The student you are trying to remove is not in this class");

        classroom.removeStudent(studentId);

        repository.save(classroom);
    }

    public void removeStudentFromAllClassrooms(UUID studentId){
        repository.pullStudentFromAllClassrooms(studentId);
    }
}
