package com.payroll.dtos.user;

/*
 * Objeto de Transferência de Dados (DTO) para gestão de usuários.
 * Utilizado em operações de cadastro e edição (CRUD), encapsulando as regras
 * de validação (Bean Validation) para garantir a consistência dos dados de conta.
 */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {
    
    // Identificador (pode ser nulo na criação, mas necessário na edição)
    private Long id;

    // --- Identificação e Contato ---
    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres.")
    private String username;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    private String email;

    // --- Segurança e Permissões ---
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    // Define o nível de acesso (Role) no sistema (ex: ADMIN, USER)
    private String role;

    public UserRequestDTO() {}
    
    // Construtor completo para facilitar conversões e testes unitários
    public UserRequestDTO(Long id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}