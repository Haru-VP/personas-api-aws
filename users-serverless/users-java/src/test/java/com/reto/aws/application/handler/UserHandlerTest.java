package com.reto.aws.application.handler;

import com.reto.aws.application.dto.request.UserRequestDto;
import com.reto.aws.application.dto.response.UserResponseDto;
import com.reto.aws.application.mapper.IUserDtoMapper;
import com.reto.aws.domain.exception.UserNotFoundException;
import com.reto.aws.domain.model.User;
import com.reto.aws.domain.serviceport.IUserServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    private static final String ID = "1";
    private static final String NAME = "Ana";
    private static final String EMAIL = "ana@mail.com";

    @Mock
    private IUserServicePort userServicePort;

    private UserHandler userHandler;

    @BeforeEach
    void setUp() {
        userHandler = new UserHandler(userServicePort, Mappers.getMapper(IUserDtoMapper.class));
    }

    @Test
    void createUser_mapsRequestToDomainAndReturnsResponse() {
        when(userServicePort.createUser(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return new User(ID, user.getName(), user.getEmail());
                });

        UserResponseDto response = userHandler.createUser(new UserRequestDto(NAME, EMAIL));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userServicePort).createUser(captor.capture());
        User sentToDomain = captor.getValue();
        assertNull(sentToDomain.getId());
        assertEquals(NAME, sentToDomain.getName());
        assertEquals(EMAIL, sentToDomain.getEmail());
        assertEquals(new UserResponseDto(ID, NAME, EMAIL), response);
    }

    @Test
    void getUserById_returnsMappedResponse() {
        when(userServicePort.getUserById(ID)).thenReturn(new User(ID, NAME, EMAIL));

        assertEquals(new UserResponseDto(ID, NAME, EMAIL), userHandler.getUserById(ID));
    }

    @Test
    void getUserById_whenDomainThrows_propagatesException() {
        when(userServicePort.getUserById("999")).thenThrow(new UserNotFoundException("999"));

        assertThrows(UserNotFoundException.class, () -> userHandler.getUserById("999"));
    }
}
