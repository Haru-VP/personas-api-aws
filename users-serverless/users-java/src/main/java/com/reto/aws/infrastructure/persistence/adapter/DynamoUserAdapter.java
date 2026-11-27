package com.reto.aws.infrastructure.persistence.adapter;

import com.reto.aws.domain.model.User;
import com.reto.aws.domain.repositoryport.IUserPersistencePort;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;
import java.util.Optional;

public class DynamoUserAdapter implements IUserPersistencePort {

    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String EMAIL_VALUE = ":email";
    private static final String EMAIL_CONDITION = EMAIL_ATTRIBUTE + " = " + EMAIL_VALUE;

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final String emailIndexName;

    public DynamoUserAdapter(DynamoDbClient dynamoDbClient, String tableName, String emailIndexName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.emailIndexName = emailIndexName;
    }

    @Override
    public User saveUser(User user) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(toItem(user))
                .build());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(ID_ATTRIBUTE, text(id)))
                .consistentRead(true)
                .build());
        return response.hasItem() ? Optional.of(toUser(response.item())) : Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return dynamoDbClient.query(QueryRequest.builder()
                        .tableName(tableName)
                        .indexName(emailIndexName)
                        .keyConditionExpression(EMAIL_CONDITION)
                        .expressionAttributeValues(Map.of(EMAIL_VALUE, text(email)))
                        .limit(1)
                        .build())
                .count() > 0;
    }

    private Map<String, AttributeValue> toItem(User user) {
        return Map.of(
                ID_ATTRIBUTE, text(user.getId()),
                NAME_ATTRIBUTE, text(user.getName()),
                EMAIL_ATTRIBUTE, text(user.getEmail()));
    }

    private User toUser(Map<String, AttributeValue> item) {
        return new User(item.get(ID_ATTRIBUTE).s(), item.get(NAME_ATTRIBUTE).s(), item.get(EMAIL_ATTRIBUTE).s());
    }

    private AttributeValue text(String value) {
        return AttributeValue.fromS(value);
    }
}
