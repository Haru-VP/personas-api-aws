package com.reto.aws.infrastructure.entrypoint.http;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.aws.application.dto.request.UserRequestDto;
import com.reto.aws.infrastructure.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpRequestReaderTest {

    private static final String VALID_JSON = "{\"name\":\"Ana\",\"email\":\"ana@mail.com\",\"id\":\"ignorado\"}";

    private final HttpRequestReader reader = new HttpRequestReader(
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));

    @Test
    void readBody_whenValidJson_returnsDto() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().withBody(VALID_JSON).build();

        assertEquals(new UserRequestDto("Ana", "ana@mail.com"), reader.readBody(event, UserRequestDto.class));
    }

    @Test
    void readBody_whenBase64Encoded_decodesAndReturnsDto() {
        String encoded = Base64.getEncoder().encodeToString(VALID_JSON.getBytes(StandardCharsets.UTF_8));
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withBody(encoded)
                .withIsBase64Encoded(true)
                .build();

        assertEquals(new UserRequestDto("Ana", "ana@mail.com"), reader.readBody(event, UserRequestDto.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "null", "{no es json", "[1,2]"})
    void readBody_whenBodyIsMissingOrInvalid_throwsException(String body) {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().withBody(body).build();

        assertThrows(InvalidRequestException.class, () -> reader.readBody(event, UserRequestDto.class));
    }

    @Test
    void readBody_whenBase64IsInvalid_throwsException() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withBody("%%%no-base64%%%")
                .withIsBase64Encoded(true)
                .build();

        assertThrows(InvalidRequestException.class, () -> reader.readBody(event, UserRequestDto.class));
    }

    @Test
    void readPathParameter_whenPresent_returnsValue() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().withPathParameters(Map.of("id", "1")).build();

        assertEquals("1", reader.readPathParameter(event, "id"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/users", "/users/"})
    void resourceLocation_joinsRawPathAndId(String rawPath) {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().withRawPath(rawPath).build();

        assertEquals("/users/abc", reader.resourceLocation(event, "abc"));
    }

    @Test
    void resourceLocation_whenRawPathIsMissing_returnsIdFromRoot() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().build();

        assertEquals("/abc", reader.resourceLocation(event, "abc"));
    }

    @Test
    void readPathParameter_whenNoPathParameters_returnsNull() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().build();

        assertNull(reader.readPathParameter(event, "id"));
    }
}
