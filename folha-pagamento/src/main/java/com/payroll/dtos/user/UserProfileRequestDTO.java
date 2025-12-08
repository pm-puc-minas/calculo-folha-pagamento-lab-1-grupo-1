package com.payroll.dtos.user;

/*
 * Objeto de Transferência de Dados (DTO) para atualização de perfil do usuário.
 * Transporta os dados que o próprio usuário autenticado deseja alterar em sua conta,
 * permitindo, por exemplo, a redefinição segura de senha (rotação de credenciais).
 */

public class UserProfileRequestDTO {
    
    // Nova senha desejada (opcional)
    private String password;

    // --- Getters e Setters ---

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
}