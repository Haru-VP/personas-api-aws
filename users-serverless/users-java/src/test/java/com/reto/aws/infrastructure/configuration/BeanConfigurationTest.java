package com.reto.aws.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeanConfigurationTest {

    @Test
    void beans_areBuiltFromEnvironment() {
        assertNotNull(BeanConfiguration.userHandler());
        assertNotNull(BeanConfiguration.requestReader());
        assertNotNull(BeanConfiguration.responseFactory());
        assertNotNull(BeanConfiguration.exceptionHandler());
    }

    @Test
    void requiredVariable_whenPresent_returnsValue() {
        assertEquals("users-dev", BeanConfiguration.requiredVariable(name -> "users-dev", "USERS_TABLE"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void requiredVariable_whenMissing_failsFast(String value) {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> BeanConfiguration.requiredVariable(name -> value, "USERS_TABLE"));

        assertEquals("Missing environment variable USERS_TABLE", exception.getMessage());
    }
}
