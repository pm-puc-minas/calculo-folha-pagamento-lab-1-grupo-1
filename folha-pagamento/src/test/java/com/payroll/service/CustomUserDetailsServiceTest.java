package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        // Criar mock do UserRepository
        userRepository = mock(UserRepository.class);

        // Criar usuário real
        user = new User();
        user.setUsername("bernardo");
        user.setPassword("123456");
        user.setEmail("bernardo@test.com");
        user.setRole(User.Role.ADMIN);
        user.setCreatedAt(LocalDateTime.now());

        // Configurar comportamento do mock
        when(userRepository.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        // Criar instância do serviço e injetar o repositório
        customUserDetailsService = new CustomUserDetailsService();
        java.lang.reflect.Field field = CustomUserDetailsService.class.getDeclaredField("userRepository");
        field.setAccessible(true);
        field.set(customUserDetailsService, userRepository);
    }

    @Test
    @DisplayName("Carrega usuário por username com sucesso")
    // Verifica detalhes retornados para usuário válido
    void deveCarregarUsuarioPorUsernameComSucesso() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("bernardo");

        assertNotNull(userDetails);
        assertEquals("bernardo", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Lança UsernameNotFoundException para usuário inexistente")
    // Garante a exceção quando usuário não é encontrado
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("inexistente"));
    }
}
