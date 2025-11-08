package com.payroll.controller;

import com.payroll.config.JwtUtil;

import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Login com credenciais válidas gera tokens")
    // Valida que login com usuário e senha corretos retorna access e refresh tokens
    void loginDeveGerarTokensComCredenciaisValidas() {
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
    @DisplayName("Falha no login com credenciais inválidas")
    // Valida retorno UNAUTHORIZED e mensagem ao usar senha incorreta
    void loginDeveFalharComCredenciaisInvalidas() {
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
    @DisplayName("Falha no login quando usuário não existe")
    // Valida retorno UNAUTHORIZED ao tentar login para usuário não encontrado
    void loginDeveFalharUsuarioInexistente() {
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
    @DisplayName("Refresh válido gera novo access token")
    // Valida que um refresh token válido não expirado gera novo access token
    void refreshDeveGerarNovoAccessToken() {
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
    @DisplayName("Refresh expirado retorna UNAUTHORIZED")
    // Valida que refresh expirado retorna 401 com mensagem apropriada
    void refreshDeveFalharTokenExpirado() {
        when(jwtUtil.isTokenExpired("expirado")).thenReturn(true);

        Map<String, String> request = Map.of("refreshToken", "expirado");
        ResponseEntity<?> response = authController.refresh(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token expirado", response.getBody());
    }

    @Test
    @DisplayName("Refresh inválido para usuário inexistente")
    // Valida que refresh para usuário inexistente retorna 401 com mensagem adequada
    void refreshDeveFalharUsuarioInexistente() {
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
