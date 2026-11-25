package com.reto.aws.domain.usecase;

import com.reto.aws.domain.exception.InvalidUserException;
import com.reto.aws.domain.exception.UserAlreadyExistsException;
import com.reto.aws.domain.exception.UserNotFoundException;
import com.reto.aws.domain.model.User;
import com.reto.aws.domain.repositoryport.IUserPersistencePort;
import com.reto.aws.domain.serviceport.IUserServicePort;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class UserUseCase implements IUserServicePort {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]++@[^@\\s.]++(?:\\.[^@\\s.]++)++$");
    private static final String NAME_REQUIRED_MESSAGE = "El nombre es obligatorio";
    private static final String INVALID_EMAIL_MESSAGE = "El email no es válido";
    private static final String ID_REQUIRED_MESSAGE = "El id es obligatorio";

    private final IUserPersistencePort userPersistencePort;

    public UserUseCase(IUserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Override
    public User createUser(User user) {
        String name = normalizeName(user.getName());
        String email = normalizeEmail(user.getEmail());
        if (userPersistencePort.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
        return userPersistencePort.saveUser(new User(UUID.randomUUID().toString(), name, email));
    }

    @Override
    public User getUserById(String id) {
        if (isBlank(id)) {
            throw new InvalidUserException(ID_REQUIRED_MESSAGE);
        }
        return userPersistencePort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private String normalizeName(String name) {
        if (isBlank(name)) {
            throw new InvalidUserException(NAME_REQUIRED_MESSAGE);
        }
        return name.strip();
    }

    private String normalizeEmail(String email) {
        if (isBlank(email)) {
            throw new InvalidUserException(INVALID_EMAIL_MESSAGE);
        }
        String normalized = email.strip().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidUserException(INVALID_EMAIL_MESSAGE);
        }
        return normalized;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
