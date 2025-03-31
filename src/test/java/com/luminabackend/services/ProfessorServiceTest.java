package com.luminabackend.services;

import com.luminabackend.models.user.Professor;
import com.luminabackend.repositories.professor.ProfessorRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {
    @Mock
    ProfessorRepository repository;

    @Mock
    UserService userService;

    @Mock
    ClassroomService classroomService;

    @InjectMocks
    ProfessorService sut;

    static Professor professor;

    @BeforeAll
    static void beforeAll() {
        professor = new Professor("john@mail.com", "9fds4hfa", "John", "Doe");
    }
}
