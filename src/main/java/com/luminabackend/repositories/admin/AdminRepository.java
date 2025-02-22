package com.luminabackend.repositories.admin;

import com.luminabackend.models.user.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository  extends MongoRepository<Admin, UUID> {
    Optional<Admin> findByEmail(String email);
}
