package com.payroll.model;

/*
 * Modelo de domínio para representação de Departamentos.
 * Organiza a estrutura corporativa, agrupando funcionários e permitindo
 * a gestão hierárquica e administrativa das equipes.
 */

import java.util.ArrayList;
import java.util.List;

public class Department {
    
    private int idDepartamento;
    private String nome;
    
    // Lista de composição: um departamento contém vários funcionários
    private List<Employee> employees;

    // Construtores
    public Department() {
        // Inicializa a lista para evitar NullPointerException ao manipular
        this.employees = new ArrayList<>();
    }

    public Department(int idDepartamento, String nome) {
        this.idDepartamento = idDepartamento;
        this.nome = nome;
        this.employees = new ArrayList<>();
    }

    // Gerenciamento da lista interna de membros
    public void adicionarFuncionario(Employee employee) {
        employees.add(employee);
    }

    public void removerFuncionario(Employee employee) {
        employees.remove(employee);
    }

    // Retorna uma cópia da lista para proteger o encapsulamento (Defensive Copy)
    public List<Employee> listarFuncionarios() {
        return new ArrayList<>(employees);
    }

    // Gera um resumo textual do estado atual do departamento
    public String exibirInformacoes() {
        return "Department: " + nome + " (ID: " + idDepartamento + ") - " + employees.size() + " funcionários";
    }

    // --- Getters e Setters ---

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Employee> getFuncionarios() {
        return employees;
    }

    public void setFuncionarios(List<Employee> employees) {
        this.employees = employees;
    }
}