package com.payroll.controller;

import com.payroll.config.JwtUtil;
import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Login com geração de tokens
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userService.findByUsername(username).orElse(null);
        if (user == null || !userService.validatePassword(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        // Claims personalizadas
        Map<String, Object> claims = Map.of(
                "idUsuario", user.getId(),
                "perfil", user.getRole().name()
        );

        String accessToken = jwtUtil.generateAccessToken(username, claims);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", user
        ));
    }

    // Endpoint para refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expirado");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário inválido");
        }

        // Criar novo access token
        Map<String, Object> claims = Map.of(
                "idUsuario", user.getId(),
                "perfil", user.getRole().name()
        );
        String newAccessToken = jwtUtil.generateAccessToken(username, claims);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }
}

