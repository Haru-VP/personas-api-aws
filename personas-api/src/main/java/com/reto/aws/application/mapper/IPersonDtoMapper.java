package com.reto.aws.application.mapper;

import com.reto.aws.application.dto.request.PersonRequestDto;
import com.reto.aws.application.dto.response.PersonResponseDto;
import com.reto.aws.domain.model.Person;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPersonDtoMapper {

    Person toPerson(PersonRequestDto personRequestDto);

    PersonResponseDto toResponseDto(Person person);
}
