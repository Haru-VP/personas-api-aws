package com.reto.aws.infrastructure.persistence.adapter;

import com.reto.aws.domain.model.Person;
import com.reto.aws.domain.repositoryport.IPersonPersistencePort;
import com.reto.aws.infrastructure.persistence.entity.PersonEntity;
import com.reto.aws.infrastructure.persistence.mapper.IPersonEntityMapper;
import com.reto.aws.infrastructure.persistence.repository.IPersonRepository;

import java.util.Optional;

public class PersonJpaAdapter implements IPersonPersistencePort {

    private final IPersonRepository personRepository;
    private final IPersonEntityMapper personEntityMapper;

    public PersonJpaAdapter(IPersonRepository personRepository, IPersonEntityMapper personEntityMapper) {
        this.personRepository = personRepository;
        this.personEntityMapper = personEntityMapper;
    }

    @Override
    public Person savePerson(Person person) {
        PersonEntity saved = personRepository.save(personEntityMapper.toEntity(person));
        return personEntityMapper.toPerson(saved);
    }

    @Override
    public Optional<Person> findByIdentification(String identificationNumber) {
        return personRepository.findById(identificationNumber)
                .map(personEntityMapper::toPerson);
    }

    @Override
    public boolean existsByIdentification(String identificationNumber) {
        return personRepository.existsById(identificationNumber);
    }
}
