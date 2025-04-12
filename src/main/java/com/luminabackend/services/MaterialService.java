package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.ZipProcessingException;
import com.luminabackend.models.education.material.Material;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.repositories.material.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Material> getAllMaterials(UUID classroomId) {
        return repository.findMaterialByClassroomId(classroomId);
    }

    public Page<Material> getPaginatedClassroomMaterials(UUID classroomId, Pageable page) {
        return repository.findMaterialByClassroomId(classroomId, page);
    }

    public Material getMaterialById(UUID id) {
        Optional<Material> materialById = repository.findById(id);
        if (materialById.isEmpty()) throw new EntityNotFoundException("Material not found");
        return materialById.get();
    }

    public ByteArrayResource getAllMaterialsAsZip(UUID classroomId) throws IOException {
        List<Material> materials = getAllMaterials(classroomId);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)
        ) {
            materials.forEach(material -> {
                if (material.getId() != null) {
                    GridFsResource gridFsResource = fileStorageService.getFile(material.getFileId());
                    if (gridFsResource != null)
                        addFileToZip(gridFsResource, zipOutputStream);
                }
            });
            zipOutputStream.finish();
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private void addFileToZip(GridFsResource gridFsResource, ZipOutputStream zipOutputStream) {
        try {
            ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(gridFsResource.getFilename()));
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(gridFsResource.getInputStream().readAllBytes());
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new ZipProcessingException("Failed to add file to zip");
        }
    }

    @Transactional
    public Material saveMaterial(UUID classroomId, UUID professorId, String title, String description, MultipartFile file) throws IOException {
        String fileId = fileStorageService.storeFile(file, classroomId);
        Material material = new Material(classroomId, professorId, title, description, fileId);
        return repository.save(material);
    }

    @Transactional
    public void deleteById(UUID materialId) {
        Material material = getMaterialById(materialId);
        if (material.getFileId() != null) fileStorageService.deleteFile(material.getFileId());
        repository.deleteById(materialId);
    }

    @Transactional
    public void deleteAllByClassroomId(UUID classroomId) {
        List<Material> materialByClassroomId = repository.findMaterialByClassroomId(classroomId);
        materialByClassroomId.forEach(material -> {
            if (material.getFileId() != null) fileStorageService.deleteFile(material.getFileId());
            repository.deleteById(material.getId());
        });
    }
}
