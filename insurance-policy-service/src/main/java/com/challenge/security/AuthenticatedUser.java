package com.challenge.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AuthenticatedUser {
    private UUID id;
}
