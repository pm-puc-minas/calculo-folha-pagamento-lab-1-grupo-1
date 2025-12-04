package com.payroll.controller;

import com.payroll.config.JwtUtil;
import com.payroll.entity.User;
import com.payroll.entity.User.Role;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements IAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /** LOGIN **/
    @PostMapping("/login")
    @Override
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        User user = extractUserByUsernameOrEmail(username, email);
        if (user == null || !userService.validatePassword(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas"));
        }

        Map<String, Object> claims = buildClaims(user);
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", buildSafeUser(user)
        ));
    }

    /** REFRESH TOKEN **/
    @PostMapping("/refresh")
    @Override
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || jwtUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token inválido ou expirado"));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuário inválido"));
        }

        String newAccessToken = jwtUtil.generateAccessToken(username, buildClaims(user));
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    /** REGISTER **/
    @PostMapping("/register")
    @Override
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String roleStr = request.get("role");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email e senha são obrigatórios"));
        }
        if (password.length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "A senha deve ter pelo menos 6 caracteres"));
        }
        if (userService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email já está em uso"));
        }

        username = generateUniqueUsername(username, email);

        Role role = Role.USER;
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                role = Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        User created = userService.createUser(new User(username, email, password, role), null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("user", buildSafeUser(created)));
    }

    /** --------------------- MÉTODOS PRIVADOS --------------------- **/

    private User extractUserByUsernameOrEmail(String username, String email) {
        if (username != null && !username.isBlank()) {
            return userService.findByUsername(username).orElse(null);
        } else if (email != null && !email.isBlank()) {
            return userService.findByEmail(email).orElse(null);
        }
        return null;
    }

    private Map<String, Object> buildClaims(User user) {
        return Map.of(
                "idUsuario", user.getId(),
                "perfil", user.getRole().name()
        );
    }

    private Map<String, Object> buildSafeUser(User user) {
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("username", user.getUsername());
        safeUser.put("email", user.getEmail());
        safeUser.put("role", user.getRole());
        safeUser.put("createdAt", user.getCreatedAt());
        safeUser.put("active", user.isActive());
        return safeUser;
    }

    private String generateUniqueUsername(String username, String email) {
        if (username != null && !username.isBlank() && !userService.existsByUsername(username)) {
            return username;
        }
        String base = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        String candidate = base;
        int suffix = 1;
        while (userService.existsByUsername(candidate)) {
            candidate = base + suffix++;
        }
        return candidate;
    }
}
