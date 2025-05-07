package com.luminabackend.middlewares;

import com.luminabackend.models.user.dto.UserAccessDTO;
import com.luminabackend.services.AccessService;
import com.luminabackend.services.TokenService;
import com.luminabackend.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("resourceAccess")
public class ResourceAccessMiddleware {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccessService accessService;

    public boolean verifyClassroomAccess(String authorizationHeader, UUID classroomId) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        accessService.checkAccessToClassroomById(classroomId, new UserAccessDTO(payloadDTO));
        return true;
    }

    public boolean verifySubmissionAccess(String authorizationHeader, UUID submissionId){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        accessService.checkStudentAccessToSubmissionById(submissionId, new UserAccessDTO(payloadDTO));
        return true;
    }
}
