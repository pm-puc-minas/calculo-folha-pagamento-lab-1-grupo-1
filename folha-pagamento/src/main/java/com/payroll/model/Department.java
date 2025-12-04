package com.payroll.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @Transient
    private List<Employee> employees;

    public Department() {
        this.employees = new ArrayList<>();
    }

    public Department(Long id, String nome) {
        this.id = id;
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
        return "Department: " + nome + " (ID: " + id + ") - " + employees.size() + " funcionários";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
