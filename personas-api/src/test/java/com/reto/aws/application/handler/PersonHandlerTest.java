package com.reto.aws.application.handler;

import com.reto.aws.application.dto.request.PersonRequestDto;
import com.reto.aws.application.dto.response.PersonResponseDto;
import com.reto.aws.application.mapper.IPersonDtoMapper;
import com.reto.aws.domain.exception.PersonNotFoundException;
import com.reto.aws.domain.model.Person;
import com.reto.aws.domain.serviceport.IPersonServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonHandlerTest {

    private static final String IDENTIFICATION = "0012345";

    @Mock
    private IPersonServicePort personServicePort;

    private PersonHandler personHandler;

    @BeforeEach
    void setUp() {
        IPersonDtoMapper personDtoMapper = Mappers.getMapper(IPersonDtoMapper.class);
        personHandler = new PersonHandler(personServicePort, personDtoMapper);
    }

    @Test
    void savePerson_mapsRequestToDomainAndReturnsResponse() {
        PersonRequestDto request = new PersonRequestDto(IDENTIFICATION, "Ana", "ana@mail.com");
        when(personServicePort.savePerson(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PersonResponseDto response = personHandler.savePerson(request);

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personServicePort).savePerson(captor.capture());
        Person sentToDomain = captor.getValue();
        assertEquals(IDENTIFICATION, sentToDomain.getIdentificationNumber());
        assertEquals("Ana", sentToDomain.getName());
        assertEquals("ana@mail.com", sentToDomain.getEmail());
        assertEquals(new PersonResponseDto(IDENTIFICATION, "Ana", "ana@mail.com"), response);
    }

    @Test
    void getPersonByIdentification_returnsMappedResponse() {
        when(personServicePort.getPersonByIdentification(IDENTIFICATION))
                .thenReturn(new Person(IDENTIFICATION, "Ana", "ana@mail.com"));

        PersonResponseDto response = personHandler.getPersonByIdentification(IDENTIFICATION);

        assertEquals(new PersonResponseDto(IDENTIFICATION, "Ana", "ana@mail.com"), response);
    }

    @Test
    void getPersonByIdentification_whenDomainThrows_propagatesException() {
        when(personServicePort.getPersonByIdentification("999")).thenThrow(new PersonNotFoundException("999"));

        assertThrows(PersonNotFoundException.class, () -> personHandler.getPersonByIdentification("999"));
    }
}
