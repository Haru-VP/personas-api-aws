package com.reto.aws.domain.usecase;

import com.reto.aws.domain.exception.InvalidUserException;
import com.reto.aws.domain.exception.UserAlreadyExistsException;
import com.reto.aws.domain.exception.UserNotFoundException;
import com.reto.aws.domain.model.User;
import com.reto.aws.domain.repositoryport.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userPersistencePort);
    }

    @Test
    void createUser_whenValid_assignsIdNormalizesDataAndSavesIt() {
        when(userPersistencePort.existsByEmail("ana@mail.com")).thenReturn(false);
        when(userPersistencePort.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userUseCase.createUser(new User(null, " Ana ", " Ana@Mail.com "));

        assertNotNull(result.getId());
        assertEquals("Ana", result.getName());
        assertEquals("ana@mail.com", result.getEmail());
        verify(userPersistencePort).saveUser(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createUser_whenNameIsBlank_throwsException(String name) {
        User user = new User(null, name, "ana@mail.com");

        assertThrows(InvalidUserException.class, () -> userUseCase.createUser(user));
        verifyNoInteractions(userPersistencePort);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"ana-mail.com", "ana@mail", "ana@@mail.com", "ana @mail.com", "ana@mail..com"})
    void createUser_whenEmailIsInvalid_throwsException(String email) {
        User user = new User(null, "Ana", email);

        assertThrows(InvalidUserException.class, () -> userUseCase.createUser(user));
        verifyNoInteractions(userPersistencePort);
    }

    @Test
    void createUser_whenEmailAlreadyExists_throwsException() {
        when(userPersistencePort.existsByEmail("ana@mail.com")).thenReturn(true);
        User user = new User(null, "Ana", "Ana@Mail.com");

        assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createUser(user));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    void getUser_whenExists_returnsIt() {
        when(userPersistencePort.findById("1")).thenReturn(Optional.of(new User("1", "Ana", "ana@mail.com")));

        assertEquals("Ana", userUseCase.getUserById("1").getName());
    }

    @Test
    void getUser_whenNotExists_throwsException() {
        when(userPersistencePort.findById("999")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userUseCase.getUserById("999"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void getUser_whenIdIsBlank_throwsException(String id) {
        assertThrows(InvalidUserException.class, () -> userUseCase.getUserById(id));
        verifyNoInteractions(userPersistencePort);
    }
}
