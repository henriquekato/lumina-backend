package com.luminabackend.models.user.dto.admin;

import com.luminabackend.models.user.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@Setter
public class AdminResourceDTO extends RepresentationModel<AdminResourceDTO>{
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;

    public AdminResourceDTO(Admin admin) {
        id = admin.getId();
        email = admin.getEmail();
        firstName = admin.getFirstName();
        lastName = admin.getLastName();
    }
}
