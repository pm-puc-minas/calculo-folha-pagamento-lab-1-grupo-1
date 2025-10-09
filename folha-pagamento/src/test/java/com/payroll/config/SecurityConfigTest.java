package com.payroll.config;

import com.payroll.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        // Verifica se o encoder funciona de forma básica
        String raw = "test";
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    void authenticationManager_shouldReturnAuthenticationManager() throws Exception {
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);
        assertNotNull(manager);
        assertEquals(mockManager, manager);
    }

    @Test
    void securityFilterChain_shouldBuildWithoutErrors() throws Exception {
        // Cria um mock do HttpSecurity
        org.springframework.security.config.annotation.web.builders.HttpSecurity httpSecurity = mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class, RETURNS_DEEP_STUBS);

        // Retorna o próprio mock para chamadas encadeadas
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);

        // Agora o método não deve lançar exceção
        assertDoesNotThrow(() -> {
            SecurityFilterChain chain = securityConfig.securityFilterChain(httpSecurity);
            assertNotNull(chain);
        });
    }
}
        