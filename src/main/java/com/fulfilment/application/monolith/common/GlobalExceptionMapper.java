package com.fulfilment.application.monolith.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.common.exceptions.BusinessException;
import com.fulfilment.application.monolith.common.exceptions.ConflictException;
import com.fulfilment.application.monolith.common.exceptions.ResourceNotFoundException;
import com.fulfilment.application.monolith.common.exceptions.ValidationException;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    ObjectMapper objectMapper;

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        int code = 500;
        String message = exception.getMessage();

        // Unwrap exception to find known business exceptions
        Throwable throwable = exception;
        while (throwable != null) {
            if (throwable instanceof WebApplicationException) {
                code = ((WebApplicationException) throwable).getResponse().getStatus();
                message = throwable.getMessage();
                break;
            } else if (throwable instanceof ResourceNotFoundException) {
                code = 404;
                message = throwable.getMessage();
                break;
            } else if (throwable instanceof ValidationException) {
                code = 422;
                message = throwable.getMessage();
                break;
            } else if (throwable instanceof ConflictException) {
                code = 409;
                message = throwable.getMessage();
                break;
            }
            throwable = throwable.getCause();
        }

        if (code >= 500) {
            LOGGER.error("Internal Server Error", exception);
        } else {
            LOGGER.warnf("Static Error [%d]: %s", code, message);
        }

        ObjectNode exceptionJson = objectMapper.createObjectNode();
        exceptionJson.put("exceptionType", exception.getClass().getSimpleName());
        exceptionJson.put("code", code);
        if (message != null) {
            exceptionJson.put("error", message);
        }

        return Response.status(code).entity(exceptionJson).build();
    }
}
