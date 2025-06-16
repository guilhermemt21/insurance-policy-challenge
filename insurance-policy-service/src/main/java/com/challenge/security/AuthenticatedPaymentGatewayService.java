package com.challenge.security;

import com.challenge.core.paymentService.PaymentService;
import com.google.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.util.UUID;

public class AuthenticatedPaymentGatewayService {

    private final PaymentService paymentService;

    @Inject
    public AuthenticatedPaymentGatewayService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public AuthenticatedPaymentGateway fromHeaders(ContainerRequestContext containerRequest) {
        String token = extractBearerToken(containerRequest);
        UUID userId = paymentService.validateAndExtractUUID(token);
        return new AuthenticatedPaymentGateway(userId);
    }

    private String extractBearerToken(ContainerRequestContext containerRequest) {
        String authHeader = containerRequest.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return "";
        return authHeader.substring("Bearer ".length());
    }
}
