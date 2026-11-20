package com.reto.aws.infrastructure.controller;

import com.reto.aws.application.dto.request.PersonRequestDto;
import com.reto.aws.application.dto.response.PersonResponseDto;
import com.reto.aws.application.handler.PersonHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonHandler personHandler;

    public PersonController(PersonHandler personHandler) {
        this.personHandler = personHandler;
    }

    @PostMapping
    public ResponseEntity<PersonResponseDto> savePerson(@Valid @RequestBody PersonRequestDto personRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personHandler.savePerson(personRequestDto));
    }

    @GetMapping("/{identificationNumber}")
    public ResponseEntity<PersonResponseDto> getPerson(@PathVariable String identificationNumber) {
        return ResponseEntity.ok(personHandler.getPersonByIdentification(identificationNumber));
    }
}
