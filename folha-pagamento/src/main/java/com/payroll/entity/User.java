package com.payroll.entity;

/*
 * Entidade de persistência para Usuários do sistema.
 * Responsável por armazenar as credenciais de acesso (login), 
 * perfis de permissão (Roles) e dados de auditoria para o Spring Security.
 */

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Credenciais e Identificação ---
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true) // Garante que o nome de usuário seja exclusivo no sistema
    private String username;

    @NotBlank
    @Email
    @Column(unique = true) // Garante que o e-mail não possa ser reutilizado
    private String email;

    @NotBlank
    @Size(min = 6)
    @com.fasterxml.jackson.annotation.JsonIgnore // Segurança: Impede que o hash da senha seja serializado em respostas JSON
    private String password;

    // --- Permissões e Controle de Acesso ---
    
    @Enumerated(EnumType.STRING) // Grava o nome do Enum (ex: "ADMIN") no banco, facilitando leitura
    private Role role = Role.USER;

    // --- Auditoria e Status ---

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    private boolean active = true; // Flag para exclusão lógica (Soft Delete)

    // Construtores
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String email, String password, Role role) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Método auxiliar para compatibilidade com UserDetails (Spring Security)
    public String getName() {
        return username;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // Definição dos perfis de acesso disponíveis
    public enum Role {
        ADMIN, USER
    }
}