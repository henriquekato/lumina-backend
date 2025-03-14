package com.luminabackend.repositories.material;

import com.luminabackend.models.education.material.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface MaterialRepository extends MongoRepository<Material, UUID> {
    List<Material> findMaterialByClassroomId(UUID classroomId);
    Page<Material> findMaterialByClassroomId(UUID classroomId, Pageable page);
}
