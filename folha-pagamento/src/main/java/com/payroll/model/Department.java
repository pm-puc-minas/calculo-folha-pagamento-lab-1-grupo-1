package com.payroll.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private int idDepartamento;
    private String nome;
    private List<Employee> employees;

    // Construtores
    public Department() {
        this.employees = new ArrayList<>();
    }

    public Department(int idDepartamento, String nome) {
        this.idDepartamento = idDepartamento;
        this.nome = nome;
        this.employees = new ArrayList<>();
    }

    public void adicionarFuncionario(Employee employee) {
        employees.add(employee);
    }

    public void removerFuncionario(Employee employee) {
        employees.remove(employee);
    }

    public List<Employee> listarFuncionarios() {
        return new ArrayList<>(employees);
    }

    public String exibirInformacoes() {
        return "Department: " + nome + " (ID: " + idDepartamento + ") - " + employees.size() + " funcionários";
    }

    // Getters e Setters
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