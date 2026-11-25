package com.reto.aws.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Ya existe un usuario con email " + email);
    }
}
