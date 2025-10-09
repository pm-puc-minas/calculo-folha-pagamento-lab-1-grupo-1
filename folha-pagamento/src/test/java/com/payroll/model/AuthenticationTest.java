package com.payroll.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {

    // Testa o método autenticar() quando as credenciais são corretas
    @Test
    void testAutenticarSuccess() {
        Authentication auth = new Authentication(1, "user", "pass", "ADMIN");
        assertTrue(auth.autenticar("user", "pass"));
    }

    // Testa o método autenticar() quando as credenciais são incorretas
    @Test
    void testAutenticarFailure() {
        Authentication auth = new Authentication(1, "user", "pass", "ADMIN");

        assertFalse(auth.autenticar("user", "wrongPass"));
        assertFalse(auth.autenticar("wrongUser", "pass"));
        assertFalse(auth.autenticar("wrongUser", "wrongPass"));
    }
}
