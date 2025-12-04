package com.payroll.controller;

import com.payroll.entity.User;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints relacionados ao usuário autenticado.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // front local
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Usuario nao autenticado");
        }
        User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("Usuario nao encontrado");
        }
        Map<String, Object> safe = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "createdAt", user.getCreatedAt(),
                "active", user.isActive()
        );
        return ResponseEntity.ok(safe);
    }
}
