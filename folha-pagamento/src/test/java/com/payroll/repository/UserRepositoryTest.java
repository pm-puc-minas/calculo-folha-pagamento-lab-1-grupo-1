package com.payroll.repository;

import com.payroll.entity.User;
import org.junit.jupiter.api.BeforeEach;
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
    void testFindByUsername() {
        assertTrue(userRepository.findByUsername("bernardo").isPresent());
    }

    // Testa se é possível encontrar o usuário pelo email
    @Test
    void testFindByEmail() {
        assertTrue(userRepository.findByEmail("bernardo@example.com").isPresent());
    }

    // Testa se a verificação de existência por username funciona
    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("bernardo"));
    }

    // Testa se a verificação de existência por email funciona
    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("bernardo@example.com"));
    }

    // Testa comportamento quando o username não existe
    @Test
    void testFindByUsernameNotFound() {
        assertFalse(userRepository.findByUsername("inexistente").isPresent());
    }

    // Testa comportamento quando o email não existe
    @Test
    void testFindByEmailNotFound() {
        assertFalse(userRepository.findByEmail("naoexiste@example.com").isPresent());
    }
}
