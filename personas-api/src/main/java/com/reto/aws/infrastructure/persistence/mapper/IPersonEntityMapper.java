package com.reto.aws.infrastructure.persistence.mapper;

import com.reto.aws.domain.model.Person;
import com.reto.aws.infrastructure.persistence.entity.PersonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPersonEntityMapper {

    PersonEntity toEntity(Person person);

    Person toPerson(PersonEntity personEntity);
}
