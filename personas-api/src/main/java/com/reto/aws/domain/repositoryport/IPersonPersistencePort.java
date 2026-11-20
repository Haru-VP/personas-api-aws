package com.reto.aws.domain.repositoryport;

import com.reto.aws.domain.model.Person;

import java.util.Optional;

public interface IPersonPersistencePort {

    Person savePerson(Person person);

    Optional<Person> findByIdentification(String identificationNumber);

    boolean existsByIdentification(String identificationNumber);
}
