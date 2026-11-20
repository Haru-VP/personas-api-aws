package com.reto.aws.domain.exception;

public class PersonAlreadyExistsException extends RuntimeException {
    public PersonAlreadyExistsException(String identificationNumber) {
        super("Ya existe una persona con identificación " + identificationNumber);
    }
}
