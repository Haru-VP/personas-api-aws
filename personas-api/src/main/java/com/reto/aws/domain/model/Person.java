package com.reto.aws.domain.model;

public class Person {
    private final String identificationNumber;
    private final String name;
    private final String email;

    public Person(String identificationNumber, String name, String email) {
        this.identificationNumber = identificationNumber;
        this.name = name;
        this.email = email;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
