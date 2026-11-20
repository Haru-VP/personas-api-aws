package com.reto.aws.infrastructure.persistence.repository;

import com.reto.aws.infrastructure.persistence.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPersonRepository extends JpaRepository<PersonEntity, String> {
}
