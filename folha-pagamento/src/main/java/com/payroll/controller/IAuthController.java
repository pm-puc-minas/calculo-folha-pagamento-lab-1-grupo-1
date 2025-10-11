package com.payroll.controller;

import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Interface para operações de autenticação
 */
public interface IAuthController {
    
    /**
     * Realiza login e retorna tokens de autenticação
     * @param loginRequest Credenciais do usuário
     * @return ResponseEntity com tokens e dados do usuário
     */
    ResponseEntity<?> login(Map<String, String> loginRequest);
    
    /**
     * Renova o access token usando refresh token
     * @param request Contém o refresh token
     * @return ResponseEntity com novo access token
     */
    ResponseEntity<?> refresh(Map<String, String> request);
}