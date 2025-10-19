package com.payroll.repository;

import com.payroll.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("bernardo");
        user.setEmail("bernardo@example.com");
        user.setPassword("123456");
        userRepository.save(user);
    }

    // Testa se é possível encontrar o usuário pelo username
    @Test
    @DisplayName("Encontra usuário por username")
    // Verifica busca de usuário por username
    void deveEncontrarUsuarioPorUsername() {
        assertTrue(userRepository.findByUsername("bernardo").isPresent());
    }

    // Testa se é possível encontrar o usuário pelo email
    @Test
    @DisplayName("Encontra usuário por email")
    // Verifica busca de usuário por email
    void deveEncontrarUsuarioPorEmail() {
        assertTrue(userRepository.findByEmail("bernardo@example.com").isPresent());
    }

    // Testa se a verificação de existência por username funciona
    @Test
    @DisplayName("Verifica existência por username")
    // Confirma existência por username
    void deveVerificarExistenciaPorUsername() {
        assertTrue(userRepository.existsByUsername("bernardo"));
    }

    // Testa se a verificação de existência por email funciona
    @Test
    @DisplayName("Verifica existência por email")
    // Confirma existência por email
    void deveVerificarExistenciaPorEmail() {
        assertTrue(userRepository.existsByEmail("bernardo@example.com"));
    }

    // Testa comportamento quando o username não existe
    @Test
    @DisplayName("Retorna vazio quando username não existe")
    // Garante vazio ao buscar username inexistente
    void deveRetornarVazioQuandoUsernameNaoExiste() {
        assertFalse(userRepository.findByUsername("inexistente").isPresent());
    }

    // Testa comportamento quando o email não existe
    @Test
    @DisplayName("Retorna vazio quando email não existe")
    // Garante vazio ao buscar email inexistente
    void deveRetornarVazioQuandoEmailNaoExiste() {
        assertFalse(userRepository.findByEmail("naoexiste@example.com").isPresent());
    }
}
