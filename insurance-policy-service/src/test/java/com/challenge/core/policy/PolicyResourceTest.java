package com.challenge.core.policy;

import com.challenge.security.AuthenticatedPaymentGateway;
import com.challenge.security.AuthenticatedPaymentGatewayService;
import com.challenge.security.AuthenticatedUser;
import com.challenge.security.AuthenticatedUserService;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PolicyResourceTest {

    @Mock
    private PolicyService service;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private AuthenticatedPaymentGatewayService authenticatedPaymentGatewayService;
    @Mock
    private ContainerRequestContext ctx;

    @InjectMocks
    private PolicyResource resource;

    private UUID someId = UUID.randomUUID();
    private AuthenticatedUser mockUser = mock(AuthenticatedUser.class);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockAuthenticatedUser() {
        when(authenticatedUserService.fromHeaders(ctx)).thenReturn(mockUser);
    }

    private void mockAuthenticatedGateway() {
        when(authenticatedPaymentGatewayService.fromHeaders(ctx)).thenReturn(mock(AuthenticatedPaymentGateway.class));
    }


    @Test
    public void shouldCreatePolicyRequestWhenUserAuthenticated() {
        mockAuthenticatedUser();

        PolicyRequestDTO requestDTO = PolicyRequestDTO.builder().build();
        PolicyDTO createdDTO = PolicyDTO.builder().build();
        when(service.createPolicyRequest(eq(mockUser), eq(requestDTO))).thenReturn(createdDTO);

        Response response = resource.createPolicyRequest(ctx, requestDTO);

        assertEquals(CREATED.getStatusCode(), response.getStatus());
        assertEquals(createdDTO, response.getEntity());
    }

    @Test
    public void shouldGetPolicyRequestByIdWhenUserAuthenticated() {
        mockAuthenticatedUser();

        PolicyDTO dto = PolicyDTO.builder().build();
        when(service.getPolicyRequestById(someId)).thenReturn(dto);

        Response response = resource.getPolicyRequestById(ctx, someId);

        assertEquals(200, response.getStatus());
        assertEquals(dto, response.getEntity());
    }

    @Test
    public void shouldReturnNotFoundWhenPolicyRequestByIdNotFound() {
        mockAuthenticatedUser();

        when(service.getPolicyRequestById(someId)).thenReturn(null);

        Response response = resource.getPolicyRequestById(ctx, someId);

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void shouldGetPolicyRequestsByCustomerIdWhenUserAuthenticated() {
        mockAuthenticatedUser();

        when(service.getByCustomerId(mockUser)).thenReturn(Collections.emptyList());

        Response response = resource.getPolicyRequestsByCustomerId(ctx);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void shouldCancelPolicyRequestWhenUserAuthenticated() {
        mockAuthenticatedUser();

        Response response = resource.cancelPolicyRequest(ctx, someId);

        assertEquals(200, response.getStatus());
        verify(service).cancelPolicyRequest(someId);
    }

    @Test
    public void shouldReceiveApprovedPaymentEventWhenGatewayAuthenticated() {
        mockAuthenticatedGateway();

        Response response = resource.receiveApprovedPaymentEvent(ctx, someId);

        assertEquals(200, response.getStatus());
        verify(service).processApprovedPaymentEvent(someId);
    }

    @Test
    public void shouldReceiveDeniedPaymentEventWhenGatewayAuthenticated() {
        mockAuthenticatedGateway();

        Response response = resource.receiveDeniedPaymentEvent(ctx, someId);

        assertEquals(200, response.getStatus());
        verify(service).processDeniedPaymentEvent(someId);
    }

    @Test
    public void shouldReceiveAuthorizationEventWhenGatewayAuthenticated() {
        mockAuthenticatedGateway();

        Response response = resource.receiveAuthorizationEvent(ctx, someId);

        assertEquals(200, response.getStatus());
        verify(service).processAuthorizationEvent(someId);
    }

    @Test
    public void shouldReturnUnauthorizedWhenCreatePolicyRequestUserIsNull() {
        when(authenticatedUserService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.createPolicyRequest(ctx, PolicyRequestDTO.builder().build());
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenGetPolicyRequestByIdUserIsNull() {
        when(authenticatedUserService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.getPolicyRequestById(ctx, someId);
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenGetPolicyRequestsByCustomerIdUserIsNull() {
        when(authenticatedUserService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.getPolicyRequestsByCustomerId(ctx);
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenCancelPolicyRequestUserIsNull() {
        when(authenticatedUserService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.cancelPolicyRequest(ctx, someId);
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenReceiveApprovedPaymentEventGatewayIsNull() {
        when(authenticatedPaymentGatewayService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.receiveApprovedPaymentEvent(ctx, someId);
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenReceiveDeniedPaymentEventGatewayIsNull() {
        when(authenticatedPaymentGatewayService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.receiveDeniedPaymentEvent(ctx, someId);
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    public void shouldReturnUnauthorizedWhenReceiveAuthorizationEventGatewayIsNull() {
        when(authenticatedPaymentGatewayService.fromHeaders(ctx)).thenReturn(null);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> {
            resource.receiveAuthorizationEvent(ctx, someId);
        });
        assertEquals(UNAUTHORIZED.getStatusCode(), ex.getResponse().getStatus());
    }

}
