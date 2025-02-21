package com.luminabackend.repositories;

import com.luminabackend.models.user.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    @Aggregation(pipeline = {
            "{ '$unionWith': { 'coll': 'students', 'pipeline': [] } }",
            "{ '$unionWith': { 'coll': 'professors', 'pipeline': [] } }",
            "{ '$match': { 'username': '?0' } }",
            "{ '$limit': 1 }"
    })
    UserDetails findByUsername(String username);

    @Aggregation(pipeline = {
            "{ '$unionWith': { 'coll': 'students', 'pipeline': [] } }",
            "{ '$unionWith': { 'coll': 'professors', 'pipeline': [] } }",
            "{ '$match': { 'email': '?0' } }",
            "{ '$limit': 1 }"
    })
    Optional<User> findByEmail(String email);
}
