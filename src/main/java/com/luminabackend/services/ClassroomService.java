package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
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

    public Classroom save(ClassroomPostDTO classroomDTO){
        Classroom classroom = new Classroom(classroomDTO);

        classroom.setName(classroom.getName().trim());
        classroom.setDescription(classroom.getDescription().trim());

        return repository.save(classroom);
    }

    public Classroom save(Classroom classroom){
        return repository.save(classroom);
    }

    public List<Classroom> getAllClassrooms() {
        return repository.findAll();
    }

    public Optional<Classroom> getClassroomById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Classroom> getClassroomByName(String name){ return repository.findByName(name); }

    public boolean existsClassroomById(UUID id) {
        return repository.existsById(id);
    }

    public void deleteClassroomById(UUID id) {
        repository.deleteById(id);
    }

    public boolean studentInClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        return classroom.getStudentsIds().contains(studentId);
    }

    public Classroom addStudentToClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        if (studentInClassroom(classroomId, studentId)) return classroom;

        classroom.getStudentsIds().add(studentId);
        return repository.save(classroom);
    }

    public Classroom removeStudentFromClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId)
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

        classroom.getStudentsIds().remove(studentId);

        return repository.save(classroom);
    }
}

