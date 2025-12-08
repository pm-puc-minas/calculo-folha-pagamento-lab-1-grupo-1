package com.payroll.dtos.user;

/*
 * Objeto de Transferência de Dados (DTO) para requisição de login.
 * Encapsula as credenciais de acesso (usuário e senha) enviadas pelo cliente,
 * aplicando validações básicas antes de acionar o provedor de autenticação.
 */

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    // Credenciais de identificação do usuário
    @NotBlank(message = "O nome de usuário não pode estar em branco.")
    private String username;

    // Credencial de segredo (será processada com hash posteriormente)
    @NotBlank(message = "A senha não pode estar em branco.")
    private String password;

    public LoginRequestDTO() {}

    // Construtor utilitário para testes de integração
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getters e Setters ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}