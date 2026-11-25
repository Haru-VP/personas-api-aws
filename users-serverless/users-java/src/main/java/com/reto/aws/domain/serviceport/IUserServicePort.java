package com.reto.aws.domain.serviceport;

import com.reto.aws.domain.model.User;

public interface IUserServicePort {

    User createUser(User user);

    User getUserById(String id);
}
