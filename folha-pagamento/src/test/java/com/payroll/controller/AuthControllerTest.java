package com.payroll.controller;

import com.payroll.config.JwtUtil;
import com.payroll.dtos.user.LoginRequestDTO;
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

/**
 * Classe de teste de unidade para AuthController.
 * Utiliza Mockito para simular as dependências (UserService e JwtUtil).
 */
public class AuthControllerTest {

    // Mocks das dependências injetadas no controller
    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    // Injeta os Mocks nas instâncias de AuthController
    @InjectMocks
    private AuthController authController;

    private User user;

    /**
     * Configura o ambiente de teste antes de cada método de teste.
     * Inicializa os mocks e cria um objeto User simulado.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Criação de um usuário modelo
        user = new User();
        user.setId(1L);
        user.setUsername("bernardo");
        user.setPassword("senha123");
        user.setEmail("bernardo@example.com");
        // Nota: Assumimos que o campo Role é definido aqui se for relevante para os testes.
    }

    @Test
    @DisplayName("Login com credenciais válidas gera tokens")
    // Valida que login com usuário e senha corretos retorna access e refresh tokens
    void loginDeveGerarTokensComCredenciaisValidas() {
        // Simula o comportamento das dependências (Service e JWT) para um cenário de sucesso
        when(userService.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(userService.validatePassword("senha123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateAccessToken(eq("bernardo"), anyMap())).thenReturn("accessTokenGerado");
        when(jwtUtil.generateRefreshToken("bernardo")).thenReturn("refreshTokenGerado");

        // Cria o DTO de requisição de login
        LoginRequestDTO loginRequest = new LoginRequestDTO("bernardo", "senha123");

        // Chama o método sendo testado
        ResponseEntity<?> response = authController.login(loginRequest);
        
        // Verifica o status HTTP e a presença dos tokens na resposta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("accessTokenGerado"));
        assertTrue(response.getBody().toString().contains("refreshTokenGerado"));
        
        // Verifica se os métodos de dependência foram chamados
        verify(userService).findByUsername("bernardo");
        verify(jwtUtil).generateAccessToken(eq("bernardo"), anyMap());
    }

    @Test
    @DisplayName("Falha no login com credenciais inválidas")
    // Valida retorno UNAUTHORIZED e mensagem ao usar senha incorreta
    void loginDeveFalharComCredenciaisInvalidas() {
        // Simula que o usuário é encontrado, mas a validação da senha falha
        when(userService.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(userService.validatePassword("senhaErrada", user.getPassword())).thenReturn(false);

        // Cria o DTO com senha incorreta
        LoginRequestDTO loginRequest = new LoginRequestDTO("bernardo", "senhaErrada");

        // Chama o método
        ResponseEntity<?> response = authController.login(loginRequest);
        
        // Verifica o status HTTP e a mensagem de erro
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    @Test
    @DisplayName("Falha no login quando usuário não existe")
    // Valida retorno UNAUTHORIZED ao tentar login para usuário não encontrado
    void loginDeveFalharUsuarioInexistente() {
        // Simula que o UserService retorna Optional.empty()
        when(userService.findByUsername("inexistente")).thenReturn(Optional.empty());

        // Cria o DTO para usuário inexistente
        LoginRequestDTO loginRequest = new LoginRequestDTO("inexistente", "qualquer");
        
        // Chama o método
        ResponseEntity<?> response = authController.login(loginRequest);

        // Verifica o status HTTP e a mensagem de erro
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    @Test
    @DisplayName("Refresh válido gera novo access token")
    // Valida que um refresh token válido não expirado gera novo access token
    void refreshDeveGerarNovoAccessToken() {
        String refreshToken = "refreshValido";

        // Simula um refresh token não expirado e extrai o username
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("bernardo");
        when(userService.findByUsername("bernardo")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(eq("bernardo"), anyMap())).thenReturn("novoAccessToken");

        // Requisição para o endpoint /refresh
        Map<String, String> request = Map.of("refreshToken", refreshToken);
        ResponseEntity<?> response = authController.refresh(request);

        // Verifica o status e o novo token
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("novoAccessToken"));
    }
    
    @Test
    @DisplayName("Refresh expirado retorna UNAUTHORIZED")
    // Valida que refresh expirado retorna 401 com mensagem apropriada
    void refreshDeveFalharTokenExpirado() {
        // Simula que o token está expirado
        when(jwtUtil.isTokenExpired("expirado")).thenReturn(true);

        // Requisição para o endpoint /refresh com token expirado
        Map<String, String> request = Map.of("refreshToken", "expirado");
        ResponseEntity<?> response = authController.refresh(request);

        // Verifica o status e a mensagem de erro
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token expirado", response.getBody());
    }

    @Test
    @DisplayName("Refresh inválido para usuário inexistente")
    // Valida que refresh para usuário inexistente retorna 401 com mensagem adequada
    void refreshDeveFalharUsuarioInexistente() {
        String refreshToken = "refreshTokenValido";
        // Simula um token válido, mas que aponta para um usuário não encontrado
        when(jwtUtil.isTokenExpired(refreshToken)).thenReturn(false);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn("naoExiste");
        when(userService.findByUsername("naoExiste")).thenReturn(Optional.empty());
        
        // Requisição para o endpoint /refresh
        Map<String, String> request = Map.of("refreshToken", refreshToken);
        ResponseEntity<?> response = authController.refresh(request);
        
        // Verifica o status e a mensagem de erro
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário inválido", response.getBody());
    }
}