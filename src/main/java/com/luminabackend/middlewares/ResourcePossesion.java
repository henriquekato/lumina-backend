package com.luminabackend.middlewares;

import com.luminabackend.models.user.dto.user.UserPermissionDTO;
import com.luminabackend.services.PermissionService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("resourcePossession")
public class ResourcePossesion {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private PermissionService permissionService;

    public boolean verifyClassroomPossession(String authorizationHeader, UUID classroomId) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        permissionService.checkAccessToClassroomById(classroomId, new UserPermissionDTO(payloadDTO));
        return true;
    }
}
