package com.reto.aws.infrastructure.persistence.adapter;

import com.reto.aws.domain.model.User;
import com.reto.aws.domain.repositoryport.IUserPersistencePort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserAdapter implements IUserPersistencePort {

    private static final List<User> SEED_USERS = List.of(
            new User("1", "Ana Gómez", "ana.gomez@mail.com"),
            new User("2", "Carlos Ruiz", "carlos.ruiz@mail.com"),
            new User("3", "Laura Díaz", "laura.diaz@mail.com")
    );

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public InMemoryUserAdapter() {
        SEED_USERS.forEach(this::saveUser);
    }

    @Override
    public User saveUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
