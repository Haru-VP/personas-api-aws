package com.reto.aws.infrastructure.persistence.adapter;

import com.reto.aws.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoUserAdapterTest {

    private static final String TABLE = "users-test";
    private static final String INDEX = "email-index";
    private static final String EMAIL = "ana@mail.com";

    @Mock
    private DynamoDbClient dynamoDbClient;

    private DynamoUserAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DynamoUserAdapter(dynamoDbClient, TABLE, INDEX);
    }

    @Test
    void saveUser_putsItemWithEnglishAttributes() {
        User user = new User("1", "Ana Gómez", EMAIL);

        assertSame(user, adapter.saveUser(user));

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(dynamoDbClient).putItem(captor.capture());
        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals(Map.of(
                "id", AttributeValue.fromS("1"),
                "name", AttributeValue.fromS("Ana Gómez"),
                "email", AttributeValue.fromS(EMAIL)), captor.getValue().item());
    }

    @Test
    void findById_whenItemExists_returnsUserUsingConsistentRead() {
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder()
                .item(Map.of(
                        "id", AttributeValue.fromS("1"),
                        "name", AttributeValue.fromS("Ana Gómez"),
                        "email", AttributeValue.fromS(EMAIL)))
                .build());

        Optional<User> result = adapter.findById("1");

        assertTrue(result.isPresent());
        assertEquals("Ana Gómez", result.get().getName());
        assertEquals(EMAIL, result.get().getEmail());
        ArgumentCaptor<GetItemRequest> captor = ArgumentCaptor.forClass(GetItemRequest.class);
        verify(dynamoDbClient).getItem(captor.capture());
        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals(Map.of("id", AttributeValue.fromS("1")), captor.getValue().key());
        assertTrue(captor.getValue().consistentRead());
    }

    @Test
    void findById_whenItemDoesNotExist_returnsEmpty() {
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().build());

        assertTrue(adapter.findById("999").isEmpty());
    }

    @Test
    void existsByEmail_whenIndexHasMatch_returnsTrueQueryingTheEmailIndex() {
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(QueryResponse.builder().count(1).build());

        assertTrue(adapter.existsByEmail(EMAIL));

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(dynamoDbClient).query(captor.capture());
        QueryRequest request = captor.getValue();
        assertEquals(TABLE, request.tableName());
        assertEquals(INDEX, request.indexName());
        assertEquals("email = :email", request.keyConditionExpression());
        assertEquals(Map.of(":email", AttributeValue.fromS(EMAIL)), request.expressionAttributeValues());
        assertEquals(1, request.limit());
    }

    @Test
    void existsByEmail_whenIndexHasNoMatch_returnsFalse() {
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(QueryResponse.builder().count(0).build());

        assertFalse(adapter.existsByEmail(EMAIL));
    }
}
