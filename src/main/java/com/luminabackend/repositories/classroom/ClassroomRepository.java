package com.luminabackend.repositories.classroom;

import com.luminabackend.models.education.classroom.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends MongoRepository<Classroom, UUID> {
        Optional<Classroom> findByName(String email);
        List<Classroom> findAllByProfessorId(UUID professorId);
        List<Classroom> findAllByStudentsIdsContains(UUID id);
}
