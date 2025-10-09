package com.payroll.controller;

import com.payroll.config.JwtUtil;
import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------ LOGIN ------------------

    @Test
    void login_shouldReturnUnauthorized_whenUserNotFound() {
        Map<String, String> request = Map.of("username", "user", "password", "pass");

        when(userService.findByUsername("user")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    @Test
    void login_shouldReturnUnauthorized_whenPasswordInvalid() {
        Map<String, String> request = Map.of("username", "user", "password", "wrongpass");
        User user = new User();
        user.setPassword("hashedpass");

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(userService.validatePassword("wrongpass", "hashedpass")).thenReturn(false);

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciais inválidas", response.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    void login_shouldReturnTokens_whenCredentialsValid() {
        Map<String, String> request = Map.of("username", "user", "password", "pass");
        User user = new User();
        user.setId(1L);
        user.setRole(User.Role.USER);
        user.setPassword("hashedpass");

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(userService.validatePassword("pass", "hashedpass")).thenReturn(true);
        when(jwtUtil.generateAccessToken(eq("user"), anyMap())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("user")).thenReturn("refresh-token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("access-token", body.get("accessToken"));
        assertEquals("refresh-token", body.get("refreshToken"));
        assertEquals(user, body.get("user"));
    }

    // ------------------ REFRESH TOKEN ------------------

    @Test
    void refresh_shouldReturnUnauthorized_whenTokenExpired() {
        Map<String, String> request = Map.of("refreshToken", "token");

        when(jwtUtil.isTokenExpired("token")).thenReturn(true);

        ResponseEntity<?> response = authController.refresh(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Refresh token expirado", response.getBody());
    }

    @Test
    void refresh_shouldReturnUnauthorized_whenUserNotFound() {
        Map<String, String> request = Map.of("refreshToken", "token");

        when(jwtUtil.isTokenExpired("token")).thenReturn(false);
        when(jwtUtil.extractUsername("token")).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.refresh(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Usuário inválido", response.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    void refresh_shouldReturnNewAccessToken_whenValid() {
        Map<String, String> request = Map.of("refreshToken", "token");
        User user = new User();
        user.setId(1L);
        user.setRole(User.Role.USER);

        when(jwtUtil.isTokenExpired("token")).thenReturn(false);
        when(jwtUtil.extractUsername("token")).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(eq("user"), anyMap())).thenReturn("new-access-token");

        ResponseEntity<?> response = authController.refresh(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("new-access-token", body.get("accessToken"));
    }
}
