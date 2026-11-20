package com.reto.aws.domain.usecase;

import com.reto.aws.domain.exception.PersonAlreadyExistsException;
import com.reto.aws.domain.exception.PersonNotFoundException;
import com.reto.aws.domain.model.Person;
import com.reto.aws.domain.repositoryport.IPersonPersistencePort;
import com.reto.aws.domain.serviceport.IPersonServicePort;

public class PersonUseCase implements IPersonServicePort {

    private final IPersonPersistencePort personPersistencePort;

    public PersonUseCase(IPersonPersistencePort personPersistencePort) {
        this.personPersistencePort = personPersistencePort;
    }

    @Override
    public Person savePerson(Person person) {
        if (personPersistencePort.existsByIdentification(person.getIdentificationNumber())) {
            throw new PersonAlreadyExistsException(person.getIdentificationNumber());
        }
        return personPersistencePort.savePerson(person);
    }

    @Override
    public Person getPersonByIdentification(String identificationNumber) {
        return personPersistencePort.findByIdentification(identificationNumber)
                .orElseThrow(() -> new PersonNotFoundException(identificationNumber));
    }
}
