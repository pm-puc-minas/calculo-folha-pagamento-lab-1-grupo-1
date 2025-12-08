package com.payroll.dtos.user;

/*
 * Objeto de Transferência de Dados (DTO) para resposta de dados de usuário.
 * Utilizado para retornar informações de cadastro de usuários (admin ou comum)
 * para a interface, omitindo dados sensíveis como a senha.
 */

public class UserResponseDTO {

    private Long id;
    
    // --- Dados de Identificação ---
    private String username;
    private String email;
    
    // --- Permissões ---
    private String role; // Define o nível de acesso (ex: ADMIN, USER)
    
    public UserResponseDTO() {}

    // Construtor completo para mapeamento de entidades
    public UserResponseDTO(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}