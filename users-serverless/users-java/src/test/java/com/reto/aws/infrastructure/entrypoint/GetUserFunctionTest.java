package com.reto.aws.infrastructure.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.reto.aws.application.handler.UserHandler;
import com.reto.aws.infrastructure.configuration.BeanConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserFunctionTest {

    private final Context context = mock(Context.class);
    private final LambdaLogger logger = mock(LambdaLogger.class);

    @BeforeEach
    void setUp() {
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void handleRequest_whenUserExists_returns200WithUser() {
        APIGatewayV2HTTPResponse response = new GetUserFunction().handleRequest(eventWithId("1"), context);

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"id\":\"1\",\"name\":\"Ana Gómez\",\"email\":\"ana.gomez@mail.com\"}", response.getBody());
    }

    @Test
    void handleRequest_whenUserDoesNotExist_returns404() {
        assertEquals(404, new GetUserFunction().handleRequest(eventWithId("999"), context).getStatusCode());
    }

    @Test
    void handleRequest_whenIdIsMissing_returns400() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().build();

        assertEquals(400, new GetUserFunction().handleRequest(event, context).getStatusCode());
    }

    @Test
    void handleRequest_whenUnexpectedError_returns500() {
        UserHandler failingHandler = mock(UserHandler.class);
        when(failingHandler.getUserById(anyString())).thenThrow(new IllegalStateException("fallo"));
        GetUserFunction function = new GetUserFunction(failingHandler, BeanConfiguration.requestReader(),
                BeanConfiguration.responseFactory(), BeanConfiguration.exceptionHandler());

        assertEquals(500, function.handleRequest(eventWithId("1"), context).getStatusCode());
    }

    private APIGatewayV2HTTPEvent eventWithId(String id) {
        return APIGatewayV2HTTPEvent.builder().withPathParameters(Map.of("id", id)).build();
    }
}
