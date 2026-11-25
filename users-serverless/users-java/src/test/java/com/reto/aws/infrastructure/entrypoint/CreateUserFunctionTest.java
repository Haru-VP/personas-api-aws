package com.reto.aws.infrastructure.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateUserFunctionTest {

    private static final String USERS_RAW_PATH = "/users";

    private final CreateUserFunction function = new CreateUserFunction();
    private final Context context = mock(Context.class);
    private final LambdaLogger logger = mock(LambdaLogger.class);

    @BeforeEach
    void setUp() {
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void handleRequest_whenValidBody_returns201WithLocation() {
        APIGatewayV2HTTPResponse response = function.handleRequest(
                eventWithBody("{\"name\":\"Pedro\",\"email\":\"pedro.create@mail.com\"}"), context);

        assertEquals(201, response.getStatusCode());
        assertTrue(response.getHeaders().get("Location").startsWith(USERS_RAW_PATH + "/"));
        assertTrue(response.getBody().contains("\"email\":\"pedro.create@mail.com\""));
    }

    @Test
    void handleRequest_whenBodyIsMissing_returns400() {
        assertEquals(400, function.handleRequest(eventWithBody(null), context).getStatusCode());
    }

    @Test
    void handleRequest_whenEmailIsInvalid_returns400() {
        APIGatewayV2HTTPResponse response = function.handleRequest(
                eventWithBody("{\"name\":\"Pedro\",\"email\":\"no-es-email\"}"), context);

        assertEquals(400, response.getStatusCode());
    }

    @Test
    void handleRequest_whenEmailAlreadyExists_returns409() {
        APIGatewayV2HTTPResponse response = function.handleRequest(
                eventWithBody("{\"name\":\"Otra Ana\",\"email\":\"ANA.GOMEZ@mail.com\"}"), context);

        assertEquals(409, response.getStatusCode());
    }

    private APIGatewayV2HTTPEvent eventWithBody(String body) {
        return APIGatewayV2HTTPEvent.builder().withRawPath(USERS_RAW_PATH).withBody(body).build();
    }
}
