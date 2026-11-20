package com.reto.aws.domain.serviceport;

import com.reto.aws.domain.model.Person;

public interface IPersonServicePort {

    Person savePerson(Person person);

    Person getPersonByIdentification(String identificationNumber);
}
