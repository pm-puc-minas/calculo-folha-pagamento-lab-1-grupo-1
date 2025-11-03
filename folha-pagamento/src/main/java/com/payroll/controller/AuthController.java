package com.payroll.controller;

import com.payroll.config.JwtUtil;
import com.payroll.entity.User;
import com.payroll.service.UserService;
import com.payroll.dtos.user.LoginRequestDTO; 
import com.payroll.dtos.user.UserResponseDTO; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.validation.Valid; 

@RestController
@RequestMapping("/api/auth")
public class AuthController implements IAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    
    @PostMapping("/login")
    @Override
    
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
        
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.findByUsername(username).orElse(null);
        if (user == null || !userService.validatePassword(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        
        Map<String, Object> claims = Map.of(
                "idUsuario", user.getId(),
                "perfil", user.getRole().name()
        );

        String accessToken = jwtUtil.generateAccessToken(username, claims);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        
        UserResponseDTO userDto = new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().name()
        );

        
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", userDto 
        ));
    }

    
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