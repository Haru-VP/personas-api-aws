package com.reto.aws.infrastructure.entrypoint.http;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.aws.application.dto.response.UserResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpResponseFactoryTest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json; charset=utf-8";

    private final HttpResponseFactory factory = new HttpResponseFactory(new ObjectMapper());
    private final UserResponseDto user = new UserResponseDto("1", "Ana", "ana@mail.com");

    @Test
    void ok_returns200WithJsonBody() {
        APIGatewayV2HTTPResponse response = factory.ok(user);

        assertEquals(200, response.getStatusCode());
        assertEquals(APPLICATION_JSON, response.getHeaders().get(CONTENT_TYPE));
        assertEquals("{\"id\":\"1\",\"name\":\"Ana\",\"email\":\"ana@mail.com\"}", response.getBody());
    }

    @Test
    void created_returns201WithLocationHeader() {
        APIGatewayV2HTTPResponse response = factory.created(user, "/users/1");

        assertEquals(201, response.getStatusCode());
        assertEquals("/users/1", response.getHeaders().get("Location"));
        assertEquals(APPLICATION_JSON, response.getHeaders().get(CONTENT_TYPE));
    }

    @Test
    void error_returnsStatusAndMessage() {
        APIGatewayV2HTTPResponse response = factory.error(404, "No existe");

        assertEquals(404, response.getStatusCode());
        assertEquals("{\"message\":\"No existe\"}", response.getBody());
    }

    @Test
    void ok_whenSerializationFails_throwsIllegalState() throws JsonProcessingException {
        ObjectMapper failingMapper = mock(ObjectMapper.class);
        when(failingMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fallo") {
        });
        HttpResponseFactory failingFactory = new HttpResponseFactory(failingMapper);

        assertThrows(IllegalStateException.class, () -> failingFactory.ok(user));
    }
}
