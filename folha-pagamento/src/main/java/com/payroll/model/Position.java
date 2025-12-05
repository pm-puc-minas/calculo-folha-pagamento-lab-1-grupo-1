package com.payroll.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "positions")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cargo")
    private Long idCargo;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "salario_base", precision = 19, scale = 2)
    private BigDecimal salarioBase;

    public Position() {}

    public Position(Long idCargo, String nome, BigDecimal salarioBase) {
        this.idCargo = idCargo;
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public void ajustarSalario(BigDecimal novoSalario) {
        this.salarioBase = novoSalario;
    }

    public Long getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(Long idCargo) {
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
