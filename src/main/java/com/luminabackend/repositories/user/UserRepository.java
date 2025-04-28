package com.luminabackend.repositories.user;

import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    @Aggregation(pipeline = {
            "{ '$unionWith': { 'coll': 'students', 'pipeline': [] } }",
            "{ '$unionWith': { 'coll': 'professors', 'pipeline': [] } }",
            "{ '$unionWith': { 'coll': 'admin', 'pipeline': [] } }",
            "{ '$match': { 'email': '?0' } }",
            "{ '$limit': 1 }"
    })
    Optional<User> findByEmail(String email);

    @Aggregation(pipeline = {
            "{ '$unionWith': { 'coll': 'students', 'pipeline': [] } }",
            "{ '$unionWith': { 'coll': 'professors', 'pipeline': [] } }",
            "{ '$unionWith': { 'coll': 'admin', 'pipeline': [] } }",
            "{ '$match': { 'id': '?0' } }",
            "{ '$limit': 1 }"
    })
    Optional<User> findByUUID(UUID id);

    Page<User> findAllByRoleIn(List<Role> roles, Pageable page);
}
