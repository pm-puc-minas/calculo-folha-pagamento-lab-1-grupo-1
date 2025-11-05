package com.payroll.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;


@Entity
public class Payroll {
    private String mesReferencia;
    private BigDecimal salarioHora;
    private BigDecimal totalProventos;
    private BigDecimal totalDescontos;
    private BigDecimal salarioLiquido;
    private BigDecimal valorAdicionalPericulosidade;
    private BigDecimal valorAdicionalInsalubridade;
    private BigDecimal valorValeAlimentacao;
    private BigDecimal valorDescontoValeTransporte;
    private BigDecimal valorDescontoINSS;
    private BigDecimal valorFGTS;
    private BigDecimal valorDescontoIRRF;
    private BigDecimal baseCalculoINSS;
    private BigDecimal baseCalculoFGTS;
    private BigDecimal baseCalculoIRRF;
    private List<BigDecimal> descontos;
    private List<BigDecimal> proventos;

    // Construtor
    public Payroll() {
        this.descontos = new ArrayList<>();
        this.proventos = new ArrayList<>();
        this.totalProventos = BigDecimal.ZERO;
        this.totalDescontos = BigDecimal.ZERO;
        this.salarioLiquido = BigDecimal.ZERO;
    }

    public Payroll(String mesReferencia) {
        this();
        this.mesReferencia = mesReferencia;
    }

    public void calcular() {
        this.totalProventos = calcularTotalProventos();
        this.totalDescontos = calcularTotalDesconto();
        this.salarioLiquido = calcularSalarioLiquido();
    }

    public void adicionarDesconto(BigDecimal valor) {
        descontos.add(valor);
    }

    public void adicionarProvento(BigDecimal valor) {
        proventos.add(valor);
    }

    public BigDecimal calcularTotalDesconto() {
        BigDecimal total = BigDecimal.ZERO;
        if (valorDescontoINSS != null) total = total.add(valorDescontoINSS);
        if (valorDescontoIRRF != null) total = total.add(valorDescontoIRRF);
        if (valorDescontoValeTransporte != null) total = total.add(valorDescontoValeTransporte);

        for (BigDecimal desconto : descontos) {
            total = total.add(desconto);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotalProventos() {
        BigDecimal total = BigDecimal.ZERO;
        if (valorAdicionalPericulosidade != null) total = total.add(valorAdicionalPericulosidade);
        if (valorAdicionalInsalubridade != null) total = total.add(valorAdicionalInsalubridade);
        if (valorValeAlimentacao != null) total = total.add(valorValeAlimentacao);

        for (BigDecimal provento : proventos) {
            total = total.add(provento);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularSalarioLiquido() {
        BigDecimal bruto = totalProventos != null ? totalProventos : BigDecimal.ZERO;
        BigDecimal descontos = totalDescontos != null ? totalDescontos : BigDecimal.ZERO;
        return bruto.subtract(descontos).setScale(2, RoundingMode.HALF_UP);
    }

    // Getters e Setters
    public String getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(String mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public BigDecimal getSalarioHora() {
        return salarioHora;
    }

    public void setSalarioHora(BigDecimal salarioHora) {
        this.salarioHora = salarioHora;
    }

    public BigDecimal getTotalProventos() {
        return totalProventos;
    }

    public void setTotalProventos(BigDecimal totalProventos) {
        this.totalProventos = totalProventos;
    }

    public BigDecimal getTotalDescontos() {
        return totalDescontos;
    }

    public void setTotalDescontos(BigDecimal totalDescontos) {
        this.totalDescontos = totalDescontos;
    }

    public BigDecimal getSalarioLiquido() {
        return salarioLiquido;
    }

    public void setSalarioLiquido(BigDecimal salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }

    public BigDecimal getValorAdicionalPericulosidade() {
        return valorAdicionalPericulosidade;
    }

    public void setValorAdicionalPericulosidade(BigDecimal valorAdicionalPericulosidade) {
        this.valorAdicionalPericulosidade = valorAdicionalPericulosidade;
    }

    public BigDecimal getValorAdicionalInsalubridade() {
        return valorAdicionalInsalubridade;
    }

    public void setValorAdicionalInsalubridade(BigDecimal valorAdicionalInsalubridade) {
        this.valorAdicionalInsalubridade = valorAdicionalInsalubridade;
    }

    public BigDecimal getValorValeAlimentacao() {
        return valorValeAlimentacao;
    }

    public void setValorValeAlimentacao(BigDecimal valorValeAlimentacao) {
        this.valorValeAlimentacao = valorValeAlimentacao;
    }

    public BigDecimal getValorDescontoValeTransporte() {
        return valorDescontoValeTransporte;
    }

    public void setValorDescontoValeTransporte(BigDecimal valorDescontoValeTransporte) {
        this.valorDescontoValeTransporte = valorDescontoValeTransporte;
    }

    public BigDecimal getValorDescontoINSS() {
        return valorDescontoINSS;
    }

    public void setValorDescontoINSS(BigDecimal valorDescontoINSS) {
        this.valorDescontoINSS = valorDescontoINSS;
    }

    public BigDecimal getValorFGTS() {
        return valorFGTS;
    }

    public void setValorFGTS(BigDecimal valorFGTS) {
        this.valorFGTS = valorFGTS;
    }

    public BigDecimal getValorDescontoIRRF() {
        return valorDescontoIRRF;
    }

    public void setValorDescontoIRRF(BigDecimal valorDescontoIRRF) {
        this.valorDescontoIRRF = valorDescontoIRRF;
    }

    public BigDecimal getBaseCalculoINSS() {
        return baseCalculoINSS;
    }

    public void setBaseCalculoINSS(BigDecimal baseCalculoINSS) {
        this.baseCalculoINSS = baseCalculoINSS;
    }

    public BigDecimal getBaseCalculoFGTS() {
        return baseCalculoFGTS;
    }

    public void setBaseCalculoFGTS(BigDecimal baseCalculoFGTS) {
        this.baseCalculoFGTS = baseCalculoFGTS;
    }

    public BigDecimal getBaseCalculoIRRF() {
        return baseCalculoIRRF;
    }

    public void setBaseCalculoIRRF(BigDecimal baseCalculoIRRF) {
        this.baseCalculoIRRF = baseCalculoIRRF;
    }
}