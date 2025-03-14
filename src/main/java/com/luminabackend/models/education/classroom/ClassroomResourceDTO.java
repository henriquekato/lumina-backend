package com.luminabackend.models.education.classroom;

import com.luminabackend.controllers.ClassroomController;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Getter @Setter
public class ClassroomResourceDTO extends RepresentationModel<ClassroomResourceDTO> {
    private UUID id;
    private String name;
    private String description;
    private UUID professorId;

    public ClassroomResourceDTO(Classroom classroom) {
        this.id = classroom.getId();
        this.name = classroom.getName();
        this.description = classroom.getDescription();
        this.professorId = classroom.getProfessorId();

        add(linkTo(methodOn(ClassroomController.class).getClassroom(id, null)).withSelfRel());
        add(linkTo(methodOn(ClassroomController.class).deleteClassroom(id)).withRel("delete").withType("DELETE"));
        add(linkTo(methodOn(ClassroomController.class).addStudent(id, UUID.randomUUID(), null)).withRel("add student").withType("POST"));
        add(linkTo(methodOn(ClassroomController.class).removeStudent(id, UUID.randomUUID(), null)).withRel("remove student").withType("DELETE"));
    }
}
