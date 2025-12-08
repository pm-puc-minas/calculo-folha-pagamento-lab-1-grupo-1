package com.payroll.model;

/*
 * Modelo de domínio para gerenciamento de dados de Autenticação.
 * Encapsula as credenciais (login/senha) e o perfil de acesso do usuário,
 * centralizando a lógica básica de comparação para validação de identidade.
 */

public class Authentication {
    
    private int idUsuario;
    private String login;
    private String senha;
    private String perfil;

    // Construtores
    public Authentication() {}

    public Authentication(int idUsuario, String login, String senha, String perfil) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Verificar se as credenciais fornecidas correspondem aos dados armazenados
    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && this.senha.equals(senha);
    }

    // --- Getters e Setters ---

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}