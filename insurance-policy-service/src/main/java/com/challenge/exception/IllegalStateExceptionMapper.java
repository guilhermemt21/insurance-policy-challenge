package com.challenge.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {
    @Override
    public Response toResponse(IllegalStateException exception) {
        System.out.println("IllegalStateExceptionMapper capturou");
        return Response.status(422).entity(exception.getMessage()).build();
    }
}
