package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.student.StudentRepository;
import com.luminabackend.utils.Sublist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private StudentRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TaskService taskService;

    public Page<Student> getPaginatedStudents(Pageable page) {
        return repository.findAll(page);
    }

    public List<Student> getAllStudentsById(List<UUID> studentIds) {
        return repository.findAllById(studentIds);
    }

    public Optional<Student> getStudentById(UUID id) {
        return repository.findById(id);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Student save(UserSignupDTO studentPostDTO) {
        userService.validateUserSignupData(studentPostDTO);

        Optional<User> userByEmail = userService.getUserByEmail(studentPostDTO.email());
        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        UserNewDataDTO userNewDataDTO = userService.prepareUserDataToSave(studentPostDTO);

        Student student = new Student(userNewDataDTO);
        return repository.save(student);
    }

    public Student edit(UUID id, UserPutDTO userPutDTO) {
        Optional<Student> studentById = getStudentById(id);
        if (studentById.isEmpty()) throw new EntityNotFoundException("Student not found");

        Student student = studentById.get();
        student = (Student) userService.editUserData(student, userPutDTO);
        return repository.save(student);
    }

    @Transactional
    public void deleteById(UUID id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Student not found");
        }

        classroomService.removeStudentFromAllClassrooms(id);

        repository.deleteById(id);
    }

    public Page<Task> getStudentDoneTasks(UUID studentId, Pageable page){
        if (!existsById(studentId)) throw new EntityNotFoundException("Student not found");
        List<Task> tasks = repository.findStudentDoneTasks(studentId);
        List<Task> sublist = Sublist.getSublist(tasks, page);
        return new PageImpl<>(sublist, page, tasks.size());
    }

    public Page<Task> getStudentNotDoneTasks(UUID studentId, Pageable page){
        if (!existsById(studentId)) throw new EntityNotFoundException("Student not found");
        List<Task> tasks =  repository.findStudentNotDoneTasks(studentId, LocalDateTime.now());
        List<Task> sublist = Sublist.getSublist(tasks, page);
        return new PageImpl<>(sublist, page, tasks.size());
    }
}
