package com.reto.aws.infrastructure.exceptionhandler;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.reto.aws.domain.exception.InvalidUserException;
import com.reto.aws.domain.exception.UserAlreadyExistsException;
import com.reto.aws.domain.exception.UserNotFoundException;
import com.reto.aws.infrastructure.entrypoint.http.HttpResponseFactory;
import com.reto.aws.infrastructure.exception.InvalidRequestException;

import java.net.HttpURLConnection;

public class ApiExceptionHandler {

    private static final String INTERNAL_ERROR_MESSAGE = "Error interno del servidor";

    private final HttpResponseFactory responseFactory;

    public ApiExceptionHandler(HttpResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    public APIGatewayV2HTTPResponse toResponse(RuntimeException exception, LambdaLogger logger) {
        return switch (exception) {
            case InvalidUserException invalidUser ->
                    responseFactory.error(HttpURLConnection.HTTP_BAD_REQUEST, invalidUser.getMessage());
            case InvalidRequestException invalidRequest ->
                    responseFactory.error(HttpURLConnection.HTTP_BAD_REQUEST, invalidRequest.getMessage());
            case UserNotFoundException notFound ->
                    responseFactory.error(HttpURLConnection.HTTP_NOT_FOUND, notFound.getMessage());
            case UserAlreadyExistsException alreadyExists ->
                    responseFactory.error(HttpURLConnection.HTTP_CONFLICT, alreadyExists.getMessage());
            default -> {
                logger.log("Unexpected error: " + exception);
                yield responseFactory.error(HttpURLConnection.HTTP_INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE);
            }
        };
    }
}
