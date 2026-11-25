package com.reto.aws.infrastructure.entrypoint.http;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.aws.infrastructure.exceptionhandler.ErrorResponse;

import java.net.HttpURLConnection;
import java.util.Map;

public class HttpResponseFactory {

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String LOCATION_HEADER = "Location";
    private static final String APPLICATION_JSON = "application/json; charset=utf-8";

    private final ObjectMapper objectMapper;

    public HttpResponseFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public APIGatewayV2HTTPResponse ok(Object body) {
        return build(HttpURLConnection.HTTP_OK, body, Map.of(CONTENT_TYPE_HEADER, APPLICATION_JSON));
    }

    public APIGatewayV2HTTPResponse created(Object body, String location) {
        return build(HttpURLConnection.HTTP_CREATED, body,
                Map.of(CONTENT_TYPE_HEADER, APPLICATION_JSON, LOCATION_HEADER, location));
    }

    public APIGatewayV2HTTPResponse error(int statusCode, String message) {
        return build(statusCode, new ErrorResponse(message), Map.of(CONTENT_TYPE_HEADER, APPLICATION_JSON));
    }

    private APIGatewayV2HTTPResponse build(int statusCode, Object body, Map<String, String> headers) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(toJson(body))
                .build();
    }

    private String toJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar la respuesta", exception);
        }
    }
}
