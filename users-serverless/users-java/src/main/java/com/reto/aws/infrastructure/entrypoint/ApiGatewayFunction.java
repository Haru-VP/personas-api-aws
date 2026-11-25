package com.reto.aws.infrastructure.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.reto.aws.infrastructure.exceptionhandler.ApiExceptionHandler;

public abstract class ApiGatewayFunction implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final ApiExceptionHandler exceptionHandler;

    protected ApiGatewayFunction(ApiExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            return process(event);
        } catch (RuntimeException exception) {
            return exceptionHandler.toResponse(exception, context.getLogger());
        }
    }

    protected abstract APIGatewayV2HTTPResponse process(APIGatewayV2HTTPEvent event);
}
