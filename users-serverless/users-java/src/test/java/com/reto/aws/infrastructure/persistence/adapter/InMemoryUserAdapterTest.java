package com.reto.aws.infrastructure.persistence.adapter;

import com.reto.aws.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserAdapterTest {

    private InMemoryUserAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new InMemoryUserAdapter();
    }

    @Test
    void findById_whenSeedUser_returnsIt() {
        assertEquals("Ana Gómez", adapter.findById("1").orElseThrow().getName());
    }

    @Test
    void findById_whenUnknownId_returnsEmpty() {
        assertTrue(adapter.findById("999").isEmpty());
    }

    @Test
    void saveUser_storesUserAndMakesItFindable() {
        User user = new User("10", "Pedro", "pedro@mail.com");

        assertEquals(user, adapter.saveUser(user));
        assertEquals(user, adapter.findById("10").orElseThrow());
    }

    @Test
    void existsByEmail_whenEmailIsRegistered_returnsTrue() {
        assertTrue(adapter.existsByEmail("carlos.ruiz@mail.com"));
    }

    @Test
    void existsByEmail_whenEmailIsNotRegistered_returnsFalse() {
        assertFalse(adapter.existsByEmail("nadie@mail.com"));
    }
}
