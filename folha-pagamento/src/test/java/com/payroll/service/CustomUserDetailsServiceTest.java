package com.payroll.service;

import com.payroll.entity.User;
import com.payroll.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("bernardo");
        user.setPassword("123456");
        user.setRole(User.Role.ADMIN); // supondo que User tem enum Role
    }

    // Testa se o usuário é carregado corretamente pelo username
    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername("bernardo")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("bernardo");

        assertNotNull(userDetails);
        assertEquals("bernardo", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    // Testa se lança exceção quando o usuário não é encontrado
    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
                customUserDetailsService.loadUserByUsername("inexistente"));
    }
}
