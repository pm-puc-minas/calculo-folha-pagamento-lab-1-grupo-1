package com.payroll.controller;

/*
 * Interface de definição para o controlador de autenticação.
 * Estabelece os contratos para operações públicas de segurança (login, refresh e registro)
 * para garantir a padronização do acesso ao sistema.
 */

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface IAuthController {

    // Realizar a autenticação do usuário e retornar o token de acesso
    ResponseEntity<?> login(Map<String, String> loginRequest);

    // Renovar o token de acesso (Refresh Token) para manter a sessão ativa
    ResponseEntity<?> refresh(Map<String, String> request);

    // Registrar um novo usuário na base de dados
    ResponseEntity<?> register(Map<String, String> request);
}