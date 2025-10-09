package com.payroll.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade User.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("clevi");
        user.setPassword("123456");
        user.setEmail("clevi@example.com");
        user.setActive(true);
    }

    @Test
    void testUserGetters() {
        assertEquals(1L, user.getId());
        assertEquals("clevi", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals("clevi@example.com", user.getEmail());
        assertTrue(user.isActive());
    }
}
