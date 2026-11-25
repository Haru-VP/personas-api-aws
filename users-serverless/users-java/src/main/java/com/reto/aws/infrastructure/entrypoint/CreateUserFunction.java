package com.reto.aws.infrastructure.entrypoint;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.reto.aws.application.dto.request.UserRequestDto;
import com.reto.aws.application.dto.response.UserResponseDto;
import com.reto.aws.application.handler.UserHandler;
import com.reto.aws.infrastructure.configuration.BeanConfiguration;
import com.reto.aws.infrastructure.entrypoint.http.HttpRequestReader;
import com.reto.aws.infrastructure.entrypoint.http.HttpResponseFactory;
import com.reto.aws.infrastructure.exceptionhandler.ApiExceptionHandler;

public class CreateUserFunction extends ApiGatewayFunction {

    private final UserHandler userHandler;
    private final HttpRequestReader requestReader;
    private final HttpResponseFactory responseFactory;

    public CreateUserFunction() {
        this(BeanConfiguration.userHandler(), BeanConfiguration.requestReader(),
                BeanConfiguration.responseFactory(), BeanConfiguration.exceptionHandler());
    }

    CreateUserFunction(UserHandler userHandler, HttpRequestReader requestReader,
                       HttpResponseFactory responseFactory, ApiExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.userHandler = userHandler;
        this.requestReader = requestReader;
        this.responseFactory = responseFactory;
    }

    @Override
    protected APIGatewayV2HTTPResponse process(APIGatewayV2HTTPEvent event) {
        UserRequestDto request = requestReader.readBody(event, UserRequestDto.class);
        UserResponseDto response = userHandler.createUser(request);
        return responseFactory.created(response, requestReader.resourceLocation(event, response.id()));
    }
}
