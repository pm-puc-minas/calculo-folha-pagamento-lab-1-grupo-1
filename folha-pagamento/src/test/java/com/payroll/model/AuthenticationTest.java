package com.payroll.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {

    // Testa o método autenticar() quando as credenciais são corretas
    @Test
    @DisplayName("Autenticar com credenciais corretas retorna true")
    // Verifica autenticação com credenciais válidas
    void deveAutenticarComCredenciaisCorretas() {
        Authentication auth = new Authentication(1, "user", "pass", "ADMIN");
        assertTrue(auth.autenticar("user", "pass"));
    }

    // Testa o método autenticar() quando as credenciais são incorretas
    @Test
    @DisplayName("Autenticar com credenciais incorretas retorna false")
    // Verifica autenticação falha com credenciais inválidas
    void naoDeveAutenticarComCredenciaisInvalidas() {
        Authentication auth = new Authentication(1, "user", "pass", "ADMIN");

        assertFalse(auth.autenticar("user", "wrongPass"));
        assertFalse(auth.autenticar("wrongUser", "pass"));
        assertFalse(auth.autenticar("wrongUser", "wrongPass"));
    }
}