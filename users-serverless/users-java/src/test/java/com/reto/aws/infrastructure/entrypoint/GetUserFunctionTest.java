package com.reto.aws.infrastructure.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.reto.aws.application.handler.UserHandler;
import com.reto.aws.application.mapper.IUserDtoMapper;
import com.reto.aws.domain.usecase.UserUseCase;
import com.reto.aws.infrastructure.configuration.BeanConfiguration;
import com.reto.aws.infrastructure.persistence.adapter.InMemoryUserAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetUserFunctionTest {

    private final GetUserFunction function = functionWith(
            new UserHandler(new UserUseCase(new InMemoryUserAdapter()), Mappers.getMapper(IUserDtoMapper.class)));
    private final Context context = mock(Context.class);
    private final LambdaLogger logger = mock(LambdaLogger.class);

    @BeforeEach
    void setUp() {
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void defaultConstructor_wiresDependenciesFromBeanConfiguration() {
        assertNotNull(new GetUserFunction());
    }

    @Test
    void handleRequest_whenUserExists_returns200WithUser() {
        APIGatewayV2HTTPResponse response = function.handleRequest(eventWithId("1"), context);

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"id\":\"1\",\"name\":\"Ana Gómez\",\"email\":\"ana.gomez@mail.com\"}", response.getBody());
    }

    @Test
    void handleRequest_whenUserDoesNotExist_returns404() {
        assertEquals(404, function.handleRequest(eventWithId("999"), context).getStatusCode());
    }

    @Test
    void handleRequest_whenIdIsMissing_returns400() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().build();

        assertEquals(400, function.handleRequest(event, context).getStatusCode());
    }

    @Test
    void handleRequest_whenUnexpectedError_returns500() {
        UserHandler failingHandler = mock(UserHandler.class);
        when(failingHandler.getUserById(anyString())).thenThrow(new IllegalStateException("failure"));

        assertEquals(500, functionWith(failingHandler).handleRequest(eventWithId("1"), context).getStatusCode());
    }

    private static GetUserFunction functionWith(UserHandler userHandler) {
        return new GetUserFunction(userHandler, BeanConfiguration.requestReader(),
                BeanConfiguration.responseFactory(), BeanConfiguration.exceptionHandler());
    }

    private APIGatewayV2HTTPEvent eventWithId(String id) {
        return APIGatewayV2HTTPEvent.builder().withPathParameters(Map.of("id", id)).build();
    }
}
