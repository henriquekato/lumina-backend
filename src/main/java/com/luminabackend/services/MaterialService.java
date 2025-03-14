package com.luminabackend.services;

import com.luminabackend.exceptions.AccessDeniedException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.material.Material;
import com.luminabackend.repositories.material.MaterialRepository;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private PermissionService permissionService;


    public List<Material> getAllMaterials(UUID classroomId, PayloadDTO payloadDTO) {
        checkAccess(classroomId, payloadDTO);
        return repository.findMaterialByClassroomId(classroomId);
    }

    public Material getMaterialById(UUID id) {
        Optional<Material> materialById = repository.findById(id);
        if (materialById.isEmpty()) throw new EntityNotFoundException("Material not found");
        return materialById.get();
    }

    public Material saveMaterial(
                                    UUID classroomId,
                                    PayloadDTO payloadDTO,
                                    String title,
                                    String description,
                                    MultipartFile file) throws IOException {

        checkAccess(classroomId, payloadDTO);

        String fileId = fileStorageService.storeFile(file, classroomId);
        Material material = new Material(classroomId, payloadDTO.id(), title, description, fileId);
        return repository.save(material);
    }

    public void deleteById(UUID materialId, UUID classroomId, PayloadDTO payloadDTO) {
        checkAccess(classroomId, payloadDTO);

        Material material = getMaterialById(materialId);

        if (material.getFileId() != null)
            fileStorageService.deleteFile(material.getFileId());
        repository.deleteById(materialId);
    }

    public void deleteAllByClassroomId(UUID classroomId) {
        List<Material> materialByClassroomId = repository.findMaterialByClassroomId(classroomId);
        materialByClassroomId.forEach(material -> {
            if (material.getFileId() != null)
                fileStorageService.deleteFile(material.getFileId());
            repository.deleteById(material.getId());
        });
    }

    public void checkPermission(UUID classroomId, UUID materialId, PayloadDTO payloadDTO){
        checkAccess(classroomId, payloadDTO);
        Material material = getMaterialById(materialId);
        if (!material.getProfessorId().equals(payloadDTO.id()))
            throw new AccessDeniedException("You don't have permission to access this resource");
    }

    private void checkAccess(UUID classroomId, PayloadDTO payloadDTO) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToClassroom(payloadDTO, classroom);
    }

}
