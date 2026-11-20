package com.reto.aws.application.handler;

import com.reto.aws.application.dto.request.PersonRequestDto;
import com.reto.aws.application.dto.response.PersonResponseDto;
import com.reto.aws.application.mapper.IPersonDtoMapper;
import com.reto.aws.domain.model.Person;
import com.reto.aws.domain.serviceport.IPersonServicePort;
import org.springframework.stereotype.Service;


@Service
public class PersonHandler {

    private final IPersonServicePort personServicePort;
    private final IPersonDtoMapper personDtoMapper;

    public PersonHandler(IPersonServicePort personServicePort, IPersonDtoMapper personDtoMapper) {
        this.personServicePort = personServicePort;
        this.personDtoMapper = personDtoMapper;
    }

    public PersonResponseDto savePerson(PersonRequestDto personRequestDto) {
        Person person = personDtoMapper.toPerson(personRequestDto);
        return personDtoMapper.toResponseDto(personServicePort.savePerson(person));
    }

    public PersonResponseDto getPersonByIdentification(String identificationNumber) {
        return personDtoMapper.toResponseDto(personServicePort.getPersonByIdentification(identificationNumber));
    }
}
