package com.reto.aws.infrastructure.configuration;

import com.reto.aws.domain.repositoryport.IPersonPersistencePort;
import com.reto.aws.domain.serviceport.IPersonServicePort;
import com.reto.aws.domain.usecase.PersonUseCase;
import com.reto.aws.infrastructure.persistence.adapter.PersonJpaAdapter;
import com.reto.aws.infrastructure.persistence.mapper.IPersonEntityMapper;
import com.reto.aws.infrastructure.persistence.repository.IPersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IPersonPersistencePort personPersistencePort(IPersonRepository personRepository,
                                                        IPersonEntityMapper personEntityMapper) {
        return new PersonJpaAdapter(personRepository, personEntityMapper);
    }

    @Bean
    public IPersonServicePort personServicePort(IPersonPersistencePort personPersistencePort) {
        return new PersonUseCase(personPersistencePort);
    }
}
