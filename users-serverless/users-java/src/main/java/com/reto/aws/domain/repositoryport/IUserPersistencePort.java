package com.reto.aws.domain.repositoryport;

import com.reto.aws.domain.model.User;

import java.util.Optional;

public interface IUserPersistencePort {

    User saveUser(User user);

    Optional<User> findById(String id);

    boolean existsByEmail(String email);
}
