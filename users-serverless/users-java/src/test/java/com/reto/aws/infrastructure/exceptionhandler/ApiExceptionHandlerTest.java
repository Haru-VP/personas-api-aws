package com.reto.aws.infrastructure.exceptionhandler;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.aws.domain.exception.InvalidUserException;
import com.reto.aws.domain.exception.UserAlreadyExistsException;
import com.reto.aws.domain.exception.UserNotFoundException;
import com.reto.aws.infrastructure.entrypoint.http.HttpResponseFactory;
import com.reto.aws.infrastructure.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler(new HttpResponseFactory(new ObjectMapper()));
    private final LambdaLogger logger = mock(LambdaLogger.class);

    @Test
    void toResponse_whenInvalidUser_returns400() {
        assertEquals(400, handler.toResponse(new InvalidUserException("inválido"), logger).getStatusCode());
        verifyNoInteractions(logger);
    }

    @Test
    void toResponse_whenInvalidRequest_returns400() {
        assertEquals(400, handler.toResponse(new InvalidRequestException("sin body"), logger).getStatusCode());
    }

    @Test
    void toResponse_whenUserNotFound_returns404() {
        assertEquals(404, handler.toResponse(new UserNotFoundException("9"), logger).getStatusCode());
    }

    @Test
    void toResponse_whenUserAlreadyExists_returns409() {
        assertEquals(409, handler.toResponse(new UserAlreadyExistsException("ana@mail.com"), logger).getStatusCode());
    }

    @Test
    void toResponse_whenUnexpectedError_returns500WithoutLeakingDetailsAndLogsIt() {
        APIGatewayV2HTTPResponse response = handler.toResponse(new IllegalStateException("detalle interno"), logger);

        assertEquals(500, response.getStatusCode());
        assertFalse(response.getBody().contains("detalle interno"));
        verify(logger).log(anyString());
    }
}
