package com.payroll.model;

/*
 * Entidade de persistência que representa os Cargos da empresa.
 * Mapeia a tabela 'positions' no banco de dados, definindo a estrutura
 * de funções e os salários-base associados a cada nível hierárquico.
 */

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "positions")
public class Position {

    // Identificador único do cargo (Chave Primária)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cargo")
    private Long idCargo;

    // Nome descritivo da função (ex: "Desenvolvedor Júnior")
    @Column(nullable = false, length = 120)
    private String nome;

    // Valor de referência salarial para o cargo
    @Column(name = "salario_base", precision = 19, scale = 2)
    private BigDecimal salarioBase;

    public Position() {}

    // Construtor completo para inicialização rápida
    public Position(Long idCargo, String nome, BigDecimal salarioBase) {
        this.idCargo = idCargo;
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    // Método de negócio para atualização do piso salarial
    public void ajustarSalario(BigDecimal novoSalario) {
        this.salarioBase = novoSalario;
    }

    // --- Getters e Setters ---

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