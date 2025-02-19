package com.luminabackend.repositories.professor;

import com.luminabackend.models.user.professor.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessorRepository extends MongoRepository<Professor, UUID> {
    Optional<Professor> findByEmail(String email);
}
