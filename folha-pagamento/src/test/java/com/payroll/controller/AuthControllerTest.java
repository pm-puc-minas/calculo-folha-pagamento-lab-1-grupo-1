package com.payroll.controller;

import com.payroll.config.JwtUtil;

import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Criação de um usuário 
        user = new User();
        user.setId(1L);
        user.setUsername("bernardo");
        user.setPassword("senha123");
        user.setEmail("bernardo@example.com");
    }

    @Test
    void testLogin_Sucesso() {
        // Simula comportamento do serviço
        when(userService.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(userService.validatePassword("senha123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateAccessToken(eq("bernardo"), anyMap())).thenReturn("accessTokenGerado");
        when(jwtUtil.generateRefreshToken("bernardo")).thenReturn("refreshTokenGerado");

        Map<String, String> loginRequest = Map.of(
                "username", "bernardo",
                "password", "senha123"
        );

        ResponseEntity<?> response = authController.login(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("accessTokenGerado"));
        assertTrue(response.getBody().toString().contains("refreshTokenGerado"));
        verify(userService).findByUsername("bernardo");
        verify(jwtUtil).generateAccessToken(eq("bernardo"), anyMap());
    }

    @Test
    void testLogin_CredenciaisInvalidas() {
        when(userService.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(userService.validatePassword("senhaErrada", user.getPassword())).thenReturn(false);

        Map<String, String> loginRequest = Map.of(
                "username", "bernardo",
                "password", "senhaErrada"
        );

        ResponseEntity<?> response = authController.login(loginRequest);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    @Test
    void testLogin_UsuarioNaoEncontrado() {
        when(userService.findByUsername("inexistente")).thenReturn(Optional.empty());

        Map<String, String> loginRequest = Map.of(
                "username", "inexistente",
                "password", "qualquer"
        );
        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    @Test
    void testRefresh_Sucesso() {
        String refreshToken = "refreshValido";

        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("bernardo");
        when(userService.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(eq("bernardo"), anyMap())).thenReturn("novoAccessToken");

        Map<String, String> request = Map.of("refreshToken", refreshToken);
        ResponseEntity<?> response = authController.refresh(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("novoAccessToken"));
    }
    @Test
    void testRefresh_TokenExpirado() {
        when(jwtUtil.isTokenExpired("expirado")).thenReturn(true);

        Map<String, String> request = Map.of("refreshToken", "expirado");
        ResponseEntity<?> response = authController.refresh(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token expirado", response.getBody());}

    @Test
    void testRefresh_UsuarioNaoEncontrado() {
        String refreshToken = "refreshTokenValido";
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("naoExiste");
        when(userService.findByUsername("naoExiste")).thenReturn(Optional.empty());
        Map<String, String> request = Map.of("refreshToken", refreshToken);
        ResponseEntity<?> response = authController.refresh(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário inválido", response.getBody());
    }
}
