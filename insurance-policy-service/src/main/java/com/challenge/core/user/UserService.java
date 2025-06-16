package com.challenge.core.user;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserService {

    private static final Map<String, UUID> tokenToUserMap = new HashMap<>();

    static {
        tokenToUserMap.put("valid-token-1", UUID.fromString("11111111-1111-1111-1111-111111111111"));
        tokenToUserMap.put("valid-token-2", UUID.fromString("22222222-2222-2222-2222-222222222222"));
    }

    public UUID validateAndExtractUUID(String token) {
        UUID userId = tokenToUserMap.get(token);
        if (userId == null) {
            throw new WebApplicationException("Invalid or expired token", Response.Status.UNAUTHORIZED);
        }
        return userId;
    }
}
