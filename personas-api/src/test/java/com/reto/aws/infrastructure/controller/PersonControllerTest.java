package com.reto.aws.infrastructure.controller;

import com.reto.aws.application.dto.request.PersonRequestDto;
import com.reto.aws.application.dto.response.PersonResponseDto;
import com.reto.aws.application.handler.PersonHandler;
import com.reto.aws.domain.exception.PersonAlreadyExistsException;
import com.reto.aws.domain.exception.PersonNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    private static final String BASE_URL = "/api/v1/persons";
    private static final String IDENTIFICATION = "0012345";
    private static final String VALID_BODY = """
            {"identificationNumber":"0012345","name":"Ana","email":"ana@mail.com"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonHandler personHandler;

    @Test
    void savePerson_whenValid_returns201WithBody() throws Exception {
        when(personHandler.savePerson(any(PersonRequestDto.class)))
                .thenReturn(new PersonResponseDto(IDENTIFICATION, "Ana", "ana@mail.com"));

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identificationNumber").value(IDENTIFICATION))
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@mail.com"));
    }

    @Test
    void savePerson_whenInvalid_returns400WithFieldErrors() throws Exception {
        String invalidBody = """
                {"identificationNumber":"","name":"","email":"invalid"}
                """;

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.identificationNumber").exists())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists());

        verifyNoInteractions(personHandler);
    }

    @Test
    void savePerson_whenAlreadyExists_returns409() throws Exception {
        when(personHandler.savePerson(any(PersonRequestDto.class)))
                .thenThrow(new PersonAlreadyExistsException(IDENTIFICATION));

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Ya existe una persona con identificación " + IDENTIFICATION));
    }

    @Test
    void getPerson_whenExists_returns200WithBody() throws Exception {
        when(personHandler.getPersonByIdentification(IDENTIFICATION))
                .thenReturn(new PersonResponseDto(IDENTIFICATION, "Ana", "ana@mail.com"));

        mockMvc.perform(get(BASE_URL + "/{id}", IDENTIFICATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificationNumber").value(IDENTIFICATION))
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    void getPerson_whenNotExists_returns404() throws Exception {
        when(personHandler.getPersonByIdentification("999")).thenThrow(new PersonNotFoundException("999"));

        mockMvc.perform(get(BASE_URL + "/{id}", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No existe una persona con identificación 999"));
    }
}
