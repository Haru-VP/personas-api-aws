package com.reto.aws.infrastructure.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reto.aws.application.handler.UserHandler;
import com.reto.aws.application.mapper.IUserDtoMapper;
import com.reto.aws.domain.usecase.UserUseCase;
import com.reto.aws.infrastructure.entrypoint.http.HttpRequestReader;
import com.reto.aws.infrastructure.entrypoint.http.HttpResponseFactory;
import com.reto.aws.infrastructure.exceptionhandler.ApiExceptionHandler;
import com.reto.aws.infrastructure.persistence.adapter.DynamoUserAdapter;
import org.mapstruct.factory.Mappers;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.function.UnaryOperator;

public final class BeanConfiguration {

    private static final String USERS_TABLE_VARIABLE = "USERS_TABLE";
    private static final String USERS_EMAIL_INDEX_VARIABLE = "USERS_EMAIL_INDEX";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final HttpRequestReader REQUEST_READER = new HttpRequestReader(OBJECT_MAPPER);
    private static final HttpResponseFactory RESPONSE_FACTORY = new HttpResponseFactory(OBJECT_MAPPER);
    private static final ApiExceptionHandler EXCEPTION_HANDLER = new ApiExceptionHandler(RESPONSE_FACTORY);
    private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbClient.builder()
            .httpClient(UrlConnectionHttpClient.create())
            .build();
    private static final UserHandler USER_HANDLER = new UserHandler(
            new UserUseCase(new DynamoUserAdapter(
                    DYNAMO_DB_CLIENT,
                    requiredVariable(System::getenv, USERS_TABLE_VARIABLE),
                    requiredVariable(System::getenv, USERS_EMAIL_INDEX_VARIABLE))),
            Mappers.getMapper(IUserDtoMapper.class));

    private BeanConfiguration() {
    }

    public static UserHandler userHandler() {
        return USER_HANDLER;
    }

    public static HttpRequestReader requestReader() {
        return REQUEST_READER;
    }

    public static HttpResponseFactory responseFactory() {
        return RESPONSE_FACTORY;
    }

    public static ApiExceptionHandler exceptionHandler() {
        return EXCEPTION_HANDLER;
    }

    static String requiredVariable(UnaryOperator<String> source, String name) {
        String value = source.apply(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable " + name);
        }
        return value;
    }
}
