package com.reto.aws.infrastructure.entrypoint.http;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.aws.infrastructure.exception.InvalidRequestException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class HttpRequestReader {

    private static final String BODY_REQUIRED_MESSAGE = "El cuerpo de la petición es obligatorio";
    private static final String INVALID_BODY_MESSAGE = "El cuerpo de la petición no es un JSON válido";
    private static final String PATH_SEPARATOR = "/";

    private final ObjectMapper objectMapper;

    public HttpRequestReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readBody(APIGatewayV2HTTPEvent event, Class<T> type) {
        String body = event.getBody();
        if (body == null || body.isBlank()) {
            throw new InvalidRequestException(BODY_REQUIRED_MESSAGE);
        }
        T value = parse(decode(body, event.getIsBase64Encoded()), type);
        if (value == null) {
            throw new InvalidRequestException(BODY_REQUIRED_MESSAGE);
        }
        return value;
    }

    public String readPathParameter(APIGatewayV2HTTPEvent event, String name) {
        Map<String, String> pathParameters = event.getPathParameters();
        return pathParameters == null ? null : pathParameters.get(name);
    }

    public String resourceLocation(APIGatewayV2HTTPEvent event, String resourceId) {
        String path = event.getRawPath() == null ? "" : event.getRawPath();
        String basePath = path.endsWith(PATH_SEPARATOR) ? path : path + PATH_SEPARATOR;
        return basePath + resourceId;
    }

    private String decode(String body, boolean isBase64Encoded) {
        if (!isBase64Encoded) {
            return body;
        }
        try {
            return new String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(INVALID_BODY_MESSAGE);
        }
    }

    private <T> T parse(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new InvalidRequestException(INVALID_BODY_MESSAGE);
        }
    }
}
