package com.luminabackend.services;

import com.luminabackend.models.education.Classroom;
import com.luminabackend.models.education.ClassroomPostDTO;
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
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        return classroom.getStudentsIds().contains(studentId);
    }

    public Classroom addStudentToClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
        if (!studentInClassroom(classroomId, studentId)) {
            classroom.getStudentsIds().add(studentId);
            return repository.save(classroom);
        }
        return classroom;
    }

    public Classroom removeStudentFromClassroom(UUID classroomId, UUID studentId) {
        Classroom classroom = repository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
        classroom.getStudentsIds().remove(studentId);

        return repository.save(classroom);
    }
}

