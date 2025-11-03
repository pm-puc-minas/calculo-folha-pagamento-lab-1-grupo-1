package com.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.payroll.dtos.user.LoginRequestDTO;

import java.util.Map;

/**
 * Interface para operações de autenticação
 */
public interface IAuthController {
    
    /**
     * Realiza login e retorna tokens de autenticação
     * @param loginRequest Credenciais do usuário // Refere-se ao LoginRequestDTO
     * @return ResponseEntity com tokens e dados do usuário
     */
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest);
    
    /**
     * Renova o access token usando refresh token
     * @param request Contém o refresh token
     * @return ResponseEntity com novo access token
     */
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request);
}