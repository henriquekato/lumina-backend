package com.luminabackend.repositories;

import com.luminabackend.models.user.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends MongoRepository<Student, UUID> {
    Optional<Student> findByEmail(String email);
}
