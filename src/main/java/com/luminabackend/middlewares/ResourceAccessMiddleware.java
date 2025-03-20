package com.luminabackend.middlewares;

import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.services.PermissionService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("resourceAccess")
public class ResourceAccessMiddleware {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private PermissionService permissionService;

    public boolean verifyClassroomAccess(String authorizationHeader, UUID classroomId) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        permissionService.checkAccessToClassroomById(classroomId, new UserAccessDTO(payloadDTO));
        return true;
    }

    public boolean verifySubmissionAccess(String authorizationHeader, UUID submissionId){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        permissionService.checkStudentAccessToSubmissionById(submissionId, new UserAccessDTO(payloadDTO));
        return true;
    }
}
