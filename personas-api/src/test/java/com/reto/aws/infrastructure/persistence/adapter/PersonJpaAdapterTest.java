package com.reto.aws.infrastructure.persistence.adapter;

import com.reto.aws.domain.model.Person;
import com.reto.aws.infrastructure.persistence.entity.PersonEntity;
import com.reto.aws.infrastructure.persistence.mapper.IPersonEntityMapper;
import com.reto.aws.infrastructure.persistence.repository.IPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonJpaAdapterTest {

    private static final String IDENTIFICATION = "0012345";

    @Mock
    private IPersonRepository personRepository;

    private PersonJpaAdapter personJpaAdapter;

    @BeforeEach
    void setUp() {
        IPersonEntityMapper personEntityMapper = Mappers.getMapper(IPersonEntityMapper.class);
        personJpaAdapter = new PersonJpaAdapter(personRepository, personEntityMapper);
    }

    @Test
    void savePerson_persistsEntityAndReturnsDomain() {
        when(personRepository.save(any(PersonEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Person saved = personJpaAdapter.savePerson(new Person(IDENTIFICATION, "Ana", "ana@mail.com"));

        ArgumentCaptor<PersonEntity> captor = ArgumentCaptor.forClass(PersonEntity.class);
        verify(personRepository).save(captor.capture());
        PersonEntity persisted = captor.getValue();
        assertEquals(IDENTIFICATION, persisted.getIdentificationNumber());
        assertEquals("Ana", persisted.getName());
        assertEquals("ana@mail.com", persisted.getEmail());
        assertEquals(IDENTIFICATION, saved.getIdentificationNumber());
    }

    @Test
    void findByIdentification_whenExists_returnsDomain() {
        when(personRepository.findById(IDENTIFICATION)).thenReturn(Optional.of(buildEntity()));

        Optional<Person> result = personJpaAdapter.findByIdentification(IDENTIFICATION);

        assertTrue(result.isPresent());
        assertEquals("Ana", result.get().getName());
        assertEquals("ana@mail.com", result.get().getEmail());
    }

    @Test
    void findByIdentification_whenNotExists_returnsEmpty() {
        when(personRepository.findById("999")).thenReturn(Optional.empty());

        assertTrue(personJpaAdapter.findByIdentification("999").isEmpty());
    }

    @Test
    void existsByIdentification_delegatesToRepository() {
        when(personRepository.existsById(IDENTIFICATION)).thenReturn(true);
        when(personRepository.existsById("999")).thenReturn(false);

        assertTrue(personJpaAdapter.existsByIdentification(IDENTIFICATION));
        assertFalse(personJpaAdapter.existsByIdentification("999"));
    }

    private PersonEntity buildEntity() {
        PersonEntity entity = new PersonEntity();
        entity.setIdentificationNumber(IDENTIFICATION);
        entity.setName("Ana");
        entity.setEmail("ana@mail.com");
        return entity;
    }
}
