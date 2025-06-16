package com.challenge.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthenticatedPaymentGateway {
    private UUID id;
}
