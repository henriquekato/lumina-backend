package com.luminabackend.repositories.classroom;

import com.luminabackend.models.education.classroom.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends MongoRepository<Classroom, UUID> {
        Optional<Classroom> findByName(String email);
        List<Classroom> findAllByProfessorId(UUID professorId);
        List<Classroom> findAllByStudentsIdsContains(UUID id);
        Page<Classroom> findAllByProfessorId(UUID professorId, Pageable page);
        Page<Classroom> findAllByStudentsIdsContains(UUID id, Pageable page);

        @Query("{ 'studentsIds': ?0 }")
        @Update("{ $pull: { 'studentsIds': ?0 } }")
        void pullStudentFromAllClassrooms(UUID studentId);
}
