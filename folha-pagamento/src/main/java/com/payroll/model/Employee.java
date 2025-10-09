package com.payroll.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String nome;
    private String cpf;
    private String cargo;
    private LocalDate dataAdmissao;
    private BigDecimal salarioBaseFuncionario;
    private int numeroDependentes;
    private boolean recebeAdicionalPericulosidade;
    private GrauInsalubridade grauInsalubridade;
    private BigDecimal valorValeTransporteEntregue;
    private BigDecimal valorDiarioValeAlimentacao;
    private int diasTrabalhadosMes;
    private int horasSemana;
    private List<String> beneficios;

    public enum GrauInsalubridade {
        NENHUM, BAIXO, MEDIO, ALTO
    }

    // Construtores
    public Employee() {
        this.beneficios = new ArrayList<>();
        this.grauInsalubridade = GrauInsalubridade.NENHUM;
        this.recebeAdicionalPericulosidade = false;
        this.numeroDependentes = 0;
        this.diasTrabalhadosMes = 22;
        this.horasSemana = 40;
    }

    public Employee(String nome, String cpf, String cargo, LocalDate dataAdmissao, BigDecimal salarioBaseFuncionario) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
        this.salarioBaseFuncionario = salarioBaseFuncionario;
    }

    public String exibirDados() {
        return "Nome: " + nome + ", CPF: " + cpf + ", Position: " + cargo +
                ", Salário Base: R$ " + salarioBaseFuncionario +
                ", Data Admissão: " + dataAdmissao;
    }

    public boolean temDireitoInsalubridade() {
        return grauInsalubridade != GrauInsalubridade.NENHUM;
    }

    public int calcularTempo() {
        return Period.between(dataAdmissao, LocalDate.now()).getYears();
    }

    public void adicionarBeneficio(String beneficio) {
        beneficios.add(beneficio);
    }

    public List<String> listarBeneficios() {
        return new ArrayList<>(beneficios);
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public BigDecimal getSalarioBaseFuncionario() {
        return salarioBaseFuncionario;
    }

    public void setSalarioBaseFuncionario(BigDecimal salarioBaseFuncionario) {
        this.salarioBaseFuncionario = salarioBaseFuncionario;
    }

    public int getNumeroDependentes() {
        return numeroDependentes;
    }

    public void setNumeroDependentes(int numeroDependentes) {
        this.numeroDependentes = numeroDependentes;
    }

    public boolean isRecebeAdicionalPericulosidade() {
        return recebeAdicionalPericulosidade;
    }

    public void setRecebeAdicionalPericulosidade(boolean recebeAdicionalPericulosidade) {
        this.recebeAdicionalPericulosidade = recebeAdicionalPericulosidade;
    }

    public GrauInsalubridade getGrauInsalubridade() {
        return grauInsalubridade;
    }

    public void setGrauInsalubridade(GrauInsalubridade grauInsalubridade) {
        this.grauInsalubridade = grauInsalubridade;
    }

    public BigDecimal getValorValeTransporteEntregue() {
        return valorValeTransporteEntregue;
    }

    public void setValorValeTransporteEntregue(BigDecimal valorValeTransporteEntregue) {
        this.valorValeTransporteEntregue = valorValeTransporteEntregue;
    }

    public BigDecimal getValorDiarioValeAlimentacao() {
        return valorDiarioValeAlimentacao;
    }

    public void setValorDiarioValeAlimentacao(BigDecimal valorDiarioValeAlimentacao) {
        this.valorDiarioValeAlimentacao = valorDiarioValeAlimentacao;
    }

    public int getDiasTrabalhadosMes() {
        return diasTrabalhadosMes;
    }

    public void setDiasTrabalhadosMes(int diasTrabalhadosMes) {
        this.diasTrabalhadosMes = diasTrabalhadosMes;
    }

    public int getHorasSemana() {
        return horasSemana;
    }

    public void setHorasSemana(int horasSemana) {
        this.horasSemana = horasSemana;
    }

    public List<String> getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(List<String> beneficios) {
        this.beneficios = beneficios;
    }
}