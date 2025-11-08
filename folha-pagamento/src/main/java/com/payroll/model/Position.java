package com.payroll.model;

import java.math.BigDecimal;

public class Position {
    private int idCargo;
    private String nome;
    private BigDecimal salarioBase;

    // Construtores
    public Position() {}

    public Position(int idCargo, String nome, BigDecimal salarioBase) {
        this.idCargo = idCargo;
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public void ajustarSalario(BigDecimal novoSalario) {
        this.salarioBase = novoSalario;
    }

    // Getters e Setters
    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }
}