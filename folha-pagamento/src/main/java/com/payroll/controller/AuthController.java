package com.payroll.controller;

import com.payroll.config.JwtUtil;
import com.payroll.entity.User;
import com.payroll.entity.User.Role;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController implements IAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Login com geração de tokens
    @PostMapping("/login")
    @Override
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        User user = null;
        if (username != null && !username.isBlank()) {
            user = userService.findByUsername(username).orElse(null);
        } else if (email != null && !email.isBlank()) {
            user = userService.findByEmail(email).orElse(null);
        }
        if (user == null || !userService.validatePassword(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        // Claims personalizadas para incluir id e perfil no token
        Map<String, Object> claims = Map.of(
                "idUsuario", user.getId(),
                "perfil", user.getRole().name()
        );

        String subject = user.getUsername(); // usa username persistido
        String accessToken = jwtUtil.generateAccessToken(subject, claims);
        String refreshToken = jwtUtil.generateRefreshToken(subject);

        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("email", user.getEmail());
        safeUser.put("role", user.getRole());
        safeUser.put("createdAt", user.getCreatedAt());
        safeUser.put("active", user.isActive());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", safeUser
        ));
    }

    // Endpoint para refresh token
    @PostMapping("/refresh")
    @Override
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

    @PostMapping("/register")
    @Override
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String roleStr = request.get("role");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email e senha são obrigatórios");
        }
        if (password.length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A senha deve ter pelo menos 6 caracteres");
        }
        if (userService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email já está em uso");
        }

        // Gerar username se não fornecido, garantindo unicidade
        if (username == null || username.isBlank()) {
            String base = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            String candidate = base;
            int suffix = 1;
            while (userService.existsByUsername(candidate)) {
                candidate = base + suffix;
                suffix++;
            }
            username = candidate;
        } else {
            if (userService.existsByUsername(username)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username já está em uso");
            }
        }

        Role role = Role.USER;
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                role = Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException ex) {
                role = Role.USER;
            }
        }

        User toCreate = new User(username, email, password, role);
        User created = userService.createUser(toCreate, null);

        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", created.getId());
        safeUser.put("username", created.getUsername());
        safeUser.put("email", created.getEmail());
        safeUser.put("role", created.getRole());
        safeUser.put("createdAt", created.getCreatedAt());
        safeUser.put("active", created.isActive());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("user", safeUser));
    }
}

