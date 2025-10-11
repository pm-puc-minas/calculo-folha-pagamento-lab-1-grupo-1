package com.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface para operações do dashboard
 */
public interface IDashboardController {
    
    /**
     * Retorna dados estatísticos do dashboard
     * @param currentUser Usuário autenticado
     * @return ResponseEntity com dados do dashboard
     */
    ResponseEntity<?> getDashboardData(UserDetails currentUser);
}