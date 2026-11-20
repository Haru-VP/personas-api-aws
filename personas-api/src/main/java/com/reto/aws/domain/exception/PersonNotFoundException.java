package com.reto.aws.domain.exception;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String identificationNumber) {
        super("No existe una persona con identificación " + identificationNumber);
    }
}
