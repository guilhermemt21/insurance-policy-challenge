package com.challenge.security;

import com.challenge.core.user.UserService;
import com.google.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.util.UUID;

public class AuthenticatedUserService {

    private final UserService userService;

    @Inject
    public AuthenticatedUserService(UserService userService) {
        this.userService = userService;
    }

    public AuthenticatedUser fromHeaders(ContainerRequestContext containerRequest) {
        String token = extractBearerToken(containerRequest);
        UUID userId = userService.validateAndExtractUUID(token);
        return new AuthenticatedUser(userId);
    }

    private String extractBearerToken(ContainerRequestContext containerRequest) {
        String authHeader = containerRequest.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return "";
        return authHeader.substring("Bearer ".length());
    }
}
