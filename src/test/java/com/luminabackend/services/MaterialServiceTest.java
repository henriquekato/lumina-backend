package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.material.Material;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.repositories.material.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {
    @Mock
    MaterialRepository repository;

    @Mock
    FileStorageService fileStorageService;

    @InjectMocks
    MaterialService sut;

    static Professor professor1;
    static Professor professor2;
    static Classroom classroom1;
    static Classroom classroom2;
    static Material material1;
    static Material material2;
    static Material material3;
    static List<Material> materialsClassroom1;

    @BeforeEach
    void beforeEach(){
        professor1 = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
        professor2 = new Professor("paulo@mail.com", "fsadfsf3", "Paulo", "Doe");
        classroom1 = new Classroom(new ClassroomPostDTO("Class 1", "Description 1", professor1.getId()));
        classroom2 = new Classroom(new ClassroomPostDTO("Class 2", "Description 2", professor2.getId()));
        material1 = new Material(classroom1.getId(), professor1.getId(), "Material 1", "Initial material for classroom 1", UUID.randomUUID().toString());
        material2 = new Material(classroom1.getId(), professor1.getId(), "Material 2", "Material 2 for classroom 1", UUID.randomUUID().toString());
        material3 = new Material(classroom2.getId(), professor2.getId(), "Material 1", "Initial material for classroom 2", UUID.randomUUID().toString());
        materialsClassroom1 = List.of(material1, material2);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return a list of materials from one classroom")
    void shouldReturnAListOfMaterialsFromOneClassroom() {
        when(repository.findMaterialByClassroomId(classroom1.getId())).thenReturn(materialsClassroom1);

        List<Material> materials = sut.getAllMaterials(classroom1.getId());

        assertThat(materials).isEqualTo(materialsClassroom1);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should return material by id from a classroom")
    void shouldReturnMaterialByIdFromAClassroom() {
        when(repository.findById(material3.getId())).thenReturn(Optional.ofNullable(material3));

        Material actual = sut.getMaterialById(material3.getId());

        assertThat(actual).isEqualTo(material3);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should throw exception when material doesnt exist")
    void shouldThrowExceptionWhenMaterialDoesntExist() {
        when(repository.findById(material2.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(()->sut.getMaterialById(material2.getId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should save new material")
    void shouldSaveNewMaterial() throws IOException {
        when(fileStorageService.storeFile(any(MultipartFile.class), any(UUID.class))).thenReturn(material3.getFileId());
        when(repository.save(any(Material.class))).thenReturn(material3);

        Path path = Paths.get("src/test/resources/input.txt");
        byte[] content = Files.readAllBytes(path);
        MockMultipartFile multipartFile = new MockMultipartFile("input", "input.txt", "text/plain", content);

        sut.saveMaterial(material3.getClassroomId(), material3.getProfessorId(), material3.getTitle(), material3.getDescription(), multipartFile);

        verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class), any(UUID.class));
        verify(repository, times(1)).save(any(Material.class));
    }

    @Test
    @DisplayName("should delete material and its file")
    void shouldDeleteMaterialAndItsFile() {
        when(repository.findById(material1.getId())).thenReturn(Optional.ofNullable(material1));

        sut.deleteById(material1.getId());

        verify(repository, times(1)).deleteById(material1.getId());
        verify(fileStorageService, times(1)).deleteFile(material1.getFileId());
    }

    @Tag("UnitTest")
    @Test
    @DisplayName("should delete all materials from a classroom")
    void shouldDeleteAllMaterialsFromAClassroom() {
        when(repository.findMaterialByClassroomId(classroom1.getId())).thenReturn(materialsClassroom1);

        sut.deleteAllByClassroomId(classroom1.getId());

        verify(repository, times(materialsClassroom1.size())).deleteById(any(UUID.class));
    }
}
