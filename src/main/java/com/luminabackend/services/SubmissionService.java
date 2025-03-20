package com.luminabackend.services;

import com.luminabackend.exceptions.*;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.user.UserPermissionDTO;
import com.luminabackend.repositories.submission.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ClassroomService classroomService;

    public List<Submission> getAllSubmissions(UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);
        return repository.findAllByTaskId(taskId);
    }

    public Page<Submission> getPaginatedTaskSubmissions(UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO, Pageable page) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);
        return repository.findAllByTaskId(taskId, page);
    }

    public Submission getSubmissionById(UUID id) {
        Optional<Submission> submissionById = repository.findById(id);
        if (submissionById.isEmpty()) throw new EntityNotFoundException("Submission not found");
        return submissionById.get();
    }

    public Submission getSubmissionBasedOnUserPermission(UUID submissionId, UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO){
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);
        Submission submission = getSubmissionById(submissionId);
        checkStudentAccessToSubmission(submission, userPermissionDTO);
        return submission;
    }

    public ByteArrayResource getAllTaskSubmissionsFiles(UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO) throws IOException {
        List<Submission> submissions = getAllSubmissions(classroomId, taskId, userPermissionDTO);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

            submissions.forEach(submission -> {
                GridFsResource fileResource = fileStorageService.getFile(submission.getFileId());
                if (fileResource != null) {
                    addSubmissionToZip(submission, fileResource, zipOutputStream);
                }
            });

            zipOutputStream.finish();
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private void addSubmissionToZip(Submission submission, GridFsResource gridFsResource, ZipOutputStream zipOutputStream) {
        try {
            String zipPath = submission.getStudentId() + "/" + gridFsResource.getFilename();

            ZipEntry zipEntry = new ZipEntry(zipPath);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(gridFsResource.getInputStream().readAllBytes());
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new ZipProcessingException("Failed to add file to zip");
        }
    }

    public Submission saveSubmission(UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO, SubmissionPostDTO submissionPostDTO, MultipartFile file) throws IOException {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);

        Task task = taskService.getTaskById(taskId);
        if (task.getDueDate().isBefore(LocalDateTime.now()))
            throw new TaskDueDateExpiredException("Task due date expired");

        if (repository.existsByStudentIdAndTaskId(userPermissionDTO.id(), taskId))
            throw new TaskAlreadySubmittedException("You have already submitted this task");

        String fileId = fileStorageService.storeFile(file, taskId);
        Submission submission = new Submission(submissionPostDTO, taskId, userPermissionDTO.id(), fileId);
        return repository.save(submission);
    }

    public void deleteById(UUID submissionId, UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);

        Submission submission = getSubmissionById(submissionId);
        checkStudentAccessToSubmission(submission, userPermissionDTO);

        Task task = taskService.getTaskById(taskId);
        if (task.getDueDate().isBefore(LocalDateTime.now()))
            throw new TaskDueDateExpiredException("Task due date expired");

        if (submission.getFileId() != null)
            fileStorageService.deleteFile(submission.getFileId());
        repository.deleteById(submissionId);
    }

    public void deleteAllByTaskId(UUID taskId) {
        repository.findAllByTaskId(taskId).forEach(s -> {
              if (s.getFileId() != null) {
                  fileStorageService.deleteFile(s.getFileId());
              }
              repository.delete(s);
          });
    }

    public Submission submissionAssessment(UUID submissionId, UUID classroomId, UUID taskId, UserPermissionDTO userPermissionDTO, SubmissionAssessmentDTO submissionAssessmentDTO) {
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);
        Submission submission = getSubmissionById(submissionId);
        submission.setGrade(submissionAssessmentDTO.grade());
        return repository.save(submission);
    }

    public void checkAccessToSubmission(UUID classroomId, UUID taskId, UUID submissionId, UserPermissionDTO userPermissionDTO){
        Classroom classroom = classroomService.getClassroomById(classroomId);
        permissionService.checkAccessToTask(classroom, taskId, userPermissionDTO);
        Submission submission = getSubmissionById(submissionId);
        checkStudentAccessToSubmission(submission, userPermissionDTO);
    }

    private void checkStudentAccessToSubmission(Submission submission, UserPermissionDTO userPermissionDTO){
        if (userPermissionDTO.role().equals(Role.STUDENT) && !submission.getStudentId().equals(userPermissionDTO.id()))
            throw new AccessDeniedException("You don't have permission to access this resource");
    }
}
