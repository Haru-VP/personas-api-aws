package com.reto.aws.infrastructure.entrypoint;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.reto.aws.application.handler.UserHandler;
import com.reto.aws.infrastructure.configuration.BeanConfiguration;
import com.reto.aws.infrastructure.entrypoint.http.HttpRequestReader;
import com.reto.aws.infrastructure.entrypoint.http.HttpResponseFactory;
import com.reto.aws.infrastructure.exceptionhandler.ApiExceptionHandler;

public class GetUserFunction extends ApiGatewayFunction {

    private static final String ID_PATH_PARAMETER = "id";

    private final UserHandler userHandler;
    private final HttpRequestReader requestReader;
    private final HttpResponseFactory responseFactory;

    public GetUserFunction() {
        this(BeanConfiguration.userHandler(), BeanConfiguration.requestReader(),
                BeanConfiguration.responseFactory(), BeanConfiguration.exceptionHandler());
    }

    GetUserFunction(UserHandler userHandler, HttpRequestReader requestReader,
                    HttpResponseFactory responseFactory, ApiExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.userHandler = userHandler;
        this.requestReader = requestReader;
        this.responseFactory = responseFactory;
    }

    @Override
    protected APIGatewayV2HTTPResponse process(APIGatewayV2HTTPEvent event) {
        String id = requestReader.readPathParameter(event, ID_PATH_PARAMETER);
        return responseFactory.ok(userHandler.getUserById(id));
    }
}
