package com.payroll.controller;

import com.payroll.entity.User;
import com.payroll.entity.UserRole;
import com.payroll.repository.UserRepository;
import com.payroll.service.UserService;
import com.payroll.config.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    private User testUser;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";

        // Mantém dados e limpa usuários antes de criar o teste
        userRepository.deleteAll();

        // Cria usuário real com senha codificada, sem alterar seu UserService
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("senha123"); // senha limpa
        testUser.setRole(UserRole.USER);
        testUser.setCreatedBy(1L);

        testUser = userService.createUser(testUser, 1L);
    }

    @Test
    void loginSuccess() {
        Map<String, String> loginRequest = Map.of(
                "username", "testuser",
                "password", "senha123"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/login", loginRequest, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKeys("accessToken", "refreshToken", "user");
        assertThat(((Map<?, ?>) response.getBody().get("user")).get("username")).isEqualTo("testuser");
    }

    @Test
    void loginFailWrongPassword() {
        Map<String, String> loginRequest = Map.of(
                "username", "testuser",
                "password", "senhaErrada"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", loginRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Credenciais inválidas");
    }

    @Test
    void loginFailUnknownUser() {
        Map<String, String> loginRequest = Map.of(
                "username", "usuarioInexistente",
                "password", "senha"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", loginRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Credenciais inválidas");
    }

    @Test
    void refreshSuccess() {
        String refreshToken = jwtUtil.generateRefreshToken(testUser.getUsername());

        Map<String, String> refreshRequest = Map.of(
                "refreshToken", refreshToken
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/refresh", refreshRequest, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("accessToken");
    }

    @Test
    void refreshFailExpiredToken() {
        // Aqui, você precisa ter um método em JwtUtil para gerar token expirado para teste
        String expiredToken = jwtUtil.generateExpiredRefreshToken(testUser.getUsername());

        Map<String, String> refreshRequest = Map.of(
                "refreshToken", expiredToken
        );

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/refresh", refreshRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Refresh token expirado");
    }

    @Test
    void refreshFailInvalidUser() {
        String fakeUsername = "usuarioFake";
        String fakeRefreshToken = jwtUtil.generateRefreshToken(fakeUsername);

        Map<String, String> refreshRequest = Map.of(
                "refreshToken", fakeRefreshToken
        );

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/refresh", refreshRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo("Usuário inválido");
    }
}
