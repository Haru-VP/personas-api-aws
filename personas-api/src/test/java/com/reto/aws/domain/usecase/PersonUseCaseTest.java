package com.reto.aws.domain.usecase;

import com.reto.aws.domain.exception.PersonAlreadyExistsException;
import com.reto.aws.domain.exception.PersonNotFoundException;
import com.reto.aws.domain.model.Person;
import com.reto.aws.domain.repositoryport.IPersonPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonUseCaseTest {

    @Mock
    private IPersonPersistencePort personPersistencePort;

    private PersonUseCase personUseCase;

    private final Person person = new Person("123", "Ana", "ana@mail.com");

    @BeforeEach
    void setUp() {
        personUseCase = new PersonUseCase(personPersistencePort);
    }

    @Test
    void savePerson_whenNotExists_savesIt() {
        when(personPersistencePort.existsByIdentification("123")).thenReturn(false);
        when(personPersistencePort.savePerson(person)).thenReturn(person);

        Person result = personUseCase.savePerson(person);

        assertEquals("123", result.getIdentificationNumber());
        verify(personPersistencePort).savePerson(person);
    }

    @Test
    void savePerson_whenAlreadyExists_throwsException() {
        when(personPersistencePort.existsByIdentification("123")).thenReturn(true);

        assertThrows(PersonAlreadyExistsException.class, () -> personUseCase.savePerson(person));
        verify(personPersistencePort, never()).savePerson(any());
    }

    @Test
    void getPerson_whenExists_returnsIt() {
        when(personPersistencePort.findByIdentification("123")).thenReturn(Optional.of(person));

        assertEquals("Ana", personUseCase.getPersonByIdentification("123").getName());
    }

    @Test
    void getPerson_whenNotExists_throwsException() {
        when(personPersistencePort.findByIdentification("999")).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personUseCase.getPersonByIdentification("999"));
    }
}
