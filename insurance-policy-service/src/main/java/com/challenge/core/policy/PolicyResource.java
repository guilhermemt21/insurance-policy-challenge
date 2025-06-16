package com.challenge.core.policy;


import com.challenge.security.AuthenticatedPaymentGateway;
import com.challenge.security.AuthenticatedPaymentGatewayService;
import com.challenge.security.AuthenticatedUser;
import com.challenge.security.AuthenticatedUserService;
import com.google.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Path("/policy_requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor_ = {@Inject})
public class PolicyResource {

    private final PolicyService service;
    private final AuthenticatedUserService authenticatedUserService;
    private final AuthenticatedPaymentGatewayService authenticatedPaymentGatewayService;

    private AuthenticatedUser getAuthenticatedUserOrThrow(ContainerRequestContext ctx) {
        AuthenticatedUser user = authenticatedUserService.fromHeaders(ctx);
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return user;
    }

    private void getAuthenticatedGatewayOrThrow(ContainerRequestContext ctx) {
        AuthenticatedPaymentGateway gateway = authenticatedPaymentGatewayService.fromHeaders(ctx);
        if (gateway == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    @POST
    public Response createPolicyRequest(@Context ContainerRequestContext requestContext, PolicyRequestDTO requestDTO) {
        AuthenticatedUser user = getAuthenticatedUserOrThrow(requestContext);
        PolicyDTO created = service.createPolicyRequest(user, requestDTO);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response getPolicyRequestById(@Context ContainerRequestContext requestContext, @PathParam("id") UUID id) {
        getAuthenticatedUserOrThrow(requestContext);

        PolicyDTO policyRequest = service.getPolicyRequestById(id);
        return Optional.ofNullable(policyRequest).map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @GET
    @Path("/customer")
    public Response getPolicyRequestsByCustomerId(@Context ContainerRequestContext requestContext) {
        AuthenticatedUser user = getAuthenticatedUserOrThrow(requestContext);
        return Response.ok(service.getByCustomerId(user)).build();
    }

    @POST
    @Path("/{id}/cancel")
    public Response cancelPolicyRequest(@Context ContainerRequestContext requestContext, @PathParam("id") UUID id) {
        getAuthenticatedUserOrThrow(requestContext);
        service.cancelPolicyRequest(id);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/events/payment/approved")
    public Response receiveApprovedPaymentEvent(@Context ContainerRequestContext requestContext, @PathParam("id") UUID id) {
        getAuthenticatedGatewayOrThrow(requestContext);
        service.processApprovedPaymentEvent(id);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/events/payment/denied")
    public Response receiveDeniedPaymentEvent(@Context ContainerRequestContext requestContext, @PathParam("id") UUID id) {
        getAuthenticatedGatewayOrThrow(requestContext);
        service.processDeniedPaymentEvent(id);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/events/authorization")
    public Response receiveAuthorizationEvent(@Context ContainerRequestContext requestContext, @PathParam("id") UUID id) {
        getAuthenticatedGatewayOrThrow(requestContext);
        service.processAuthorizationEvent(id);
        return Response.ok().build();
    }
}
