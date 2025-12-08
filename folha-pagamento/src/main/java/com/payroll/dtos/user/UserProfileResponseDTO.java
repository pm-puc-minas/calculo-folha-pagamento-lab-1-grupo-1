package com.payroll.dtos.user;

/*
 * Objeto de Transferência de Dados (DTO) para resposta de perfil de usuário.
 * Expõe os dados cadastrais da conta do usuário para o frontend, garantindo
 * a exclusão de informações sensíveis (como a senha) durante a serialização.
 */

import com.payroll.entity.User;

public class UserProfileResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String role; // Perfil de acesso (ADMIN, USER, etc.)

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Pattern Factory Method: Converte a Entidade de persistência para o DTO de resposta
    public static UserProfileResponseDTO from(User user) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        
        // Mapeamento dos campos básicos
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        
        // Conversão segura do Enum de role para String
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        
        return dto;
    }
}